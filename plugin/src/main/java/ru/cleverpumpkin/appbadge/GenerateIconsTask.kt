package ru.cleverpumpkin.appbadge

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.SourceProvider
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import groovy.xml.XmlSlurper
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import ru.cleverpumpkin.appbadge.filter.AppBadgeFilter
import ru.cleverpumpkin.appbadge.utils.AppBadgeException
import ru.cleverpumpkin.appbadge.utils.ImageWriter
import ru.cleverpumpkin.appbadge.utils.ProjectUtils
import ru.cleverpumpkin.appbadge.utils.ResourceUtils
import ru.cleverpumpkin.appbadge.utils.ResourceUtils.getLauncherIcons
import java.io.File
import javax.inject.Inject

/**
 * @author Sergey Chuprin
 */
open class GenerateIconsTask @Inject constructor(
    private val variant: BaseVariant,
    private val outputDir: File,
    private val iconNames: Collection<String>,
    private val filters: List<AppBadgeFilter>
) : DefaultTask() {

    companion object {
        private const val MAIN_SOURCE_SET = "main"
        private val xmlSlurper = XmlSlurper()
        private val overlayIdx = 0
    }

    @TaskAction
    @Suppress("unused")
    fun run() {
        if (filters.isEmpty()) return
        val icons = getAllIcons().takeUnless(Set<String>::isEmpty) ?: return
        
        variant
            .sourceSets
            .flatMap(SourceProvider::getResDirectories)
            .forEach { resDir ->
                if (resDir == outputDir) return@forEach
                icons.forEach { name -> getResourcesFileTree(resDir, name).forEach(::processIcon) }
                icons.forEach { name -> getAdaptiveIconFileTree(resDir, name).forEach(::processAdaptiveIcon) }
            }
    }

    private fun processIcon(inputFile: File) {
        val outputFile = File(outputDir, "${inputFile.parentFile.name}/${inputFile.name}")
        outputFile.parentFile.mkdirs()

        ImageWriter(outputFile).run {
            read(inputFile)
            process(filters, false)
            write()
        }
    }

    private fun processAdaptiveIcon(inputFile: File) {
        
        // XML is an adaptive icon, specifying a background and foreground layer
        val xml = XmlParser().parse(inputFile)

        val foregroundNodeList = xml["foreground"] as? NodeList
            ?: throw AppBadgeException("NodeList at 'foreground' not found")
        val foregroundNode = foregroundNodeList.firstOrNull() as? Node
            ?: throw AppBadgeException("Foreground NodeList is empty")

        val drawableAttr = QName("http://schemas.android.com/apk/res/android", "drawable")

        // We replace the foreground drawable with a generated layer-list drawable,
        // containing (1) the original (vector) icon, and (2) the overlay.
        val attributes = foregroundNode.attributes()
        val originalResId = attributes[drawableAttr] as? String
            ?: throw AppBadgeException("Foreground drawable not found")
        val newResName = createOverlayedDrawable(originalResId, inputFile.parentFile)
        attributes[drawableAttr] = newResName

        val outputFile = File(outputDir, "${inputFile.parentFile.name}/${inputFile.name}")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(XmlUtil.serialize(xml))
    }
    
    private fun createOverlayedDrawable(originalResourceId: String, resDir: File): String {

        val newResourceName = "appicon_badged_$overlayIdx"
        val newOverlayName = "badge_overlay_$overlayIdx"
        
        // create overlay png
        val outputFile = File(outputDir, "drawable/${newOverlayName}.png")
        ImageWriter(outputFile).run {
            process(filters, true)
            write()
        }
        
        // create combined layer-list drawable
        val xmlTemplate = "<layer-list\n" +
            "    xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    >\n" +
            "    <item android:drawable=\"%s\" />\n" +
            "    <item android:drawable=\"%s\" />\n" +
            "</layer-list>\n"
        
        val overlayResourceName = "@drawable/$newOverlayName"
        val xmlString = xmlTemplate.format(originalResourceId, overlayResourceName)
        File(outputDir, resDir.name).mkdirs()
        File(outputDir, "${resDir.name}/${newResourceName}.xml").writeText(xmlString)
        
        return "@mipmap/$newResourceName"
    }

    private fun getResourcesFileTree(resDir: File, iconName: String): ConfigurableFileTree {
        return project.fileTree(resDir, {
            exclude("**/*.xml")
            include(ResourceUtils.resourceFilePattern(iconName))
        })
    }

    private fun getAdaptiveIconFileTree(resDir: File, iconName: String): FileCollection {
        return project.fileTree(resDir) {
            include(ResourceUtils.resourceFilePattern(iconName))
        }
            .filter { f -> f.absolutePath.endsWith(".xml") }
            .filter { f -> xmlSlurper.parse(f).name() == "adaptive-icon"
            }
    }

    private fun getAllIcons(): Set<String> {
        return HashSet(iconNames).apply { addAll(getLauncherIconNames()) }
    }

    private fun getLauncherIconNames(): Set<String> {
        return getAndroidManifestFiles()
            .flatMap(::getLauncherIcons)
            .toSet()
    }

    private fun getAndroidManifestFiles(): List<File> {
        val androidExtension = ProjectUtils.getAndroidExtension(project)
        return listOf(
            MAIN_SOURCE_SET,
            variant.name,
            variant.buildType.name,
            variant.flavorName
        ).mapNotNull { name ->
            when {
                name.isEmpty() -> null
                else -> {
                    val sourceSet = androidExtension.sourceSets.findByName(name)
                    sourceSet?.manifest?.srcFile?.let(project::file)?.takeIf(File::exists)
                }
            }
        }.distinctBy(File::getAbsolutePath)
    }

}