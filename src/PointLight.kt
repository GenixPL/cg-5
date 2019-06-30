import helpers.Float3
import helpers.dot
import helpers.normalize
import helpers.times
import org.apache.commons.math3.linear.MatrixUtils
import java.awt.Color
import java.lang.Math.max
import java.lang.Math.pow


class PointLight(
    val position: Float3,
    val color: Float3
) {

    /**
     * Normal vector: A vector ~n that is perpendicular to the surface and directed outwards from the surface.
     * View vector: A vector ~v that points in the direction of the viewer.
     * Light vector: A vector l that points towards the light source.
     * Reflection vector: A vector ~r that indicates the direction of pure reflection of the light vector.
     */
    fun getColor(n: Float3, pt: Float3, cameraPos: Float3, light: Light, materialColor: Float3): Color {
        val diffL = position.minus(pt)
        val normLData = arrayOf(
            doubleArrayOf(diffL.x.toDouble()),
            doubleArrayOf(diffL.y.toDouble()),
            doubleArrayOf(diffL.z.toDouble())
        )
        val NORML = MatrixUtils.createRealMatrix(normLData)
        val l = diffL / NORML.norm.toFloat()

        val diffC = cameraPos.minus(pt)
        val normCData = arrayOf(
            doubleArrayOf(diffC.x.toDouble()),
            doubleArrayOf(diffC.y.toDouble()),
            doubleArrayOf(diffC.z.toDouble())
        )
        val NORMC = MatrixUtils.createRealMatrix(normCData)
        val v = diffC / NORMC.norm.toFloat()

        val r = n.times(2 * (dot(n, l))) - l

        val first = light.ambient * materialColor
        val second = light.diffuse * max(dot(n, l), 0f) * color
        val third = color.times(light.specular) * pow(max(dot(v, r), 0f).toDouble(), light.shininess.toDouble()).toFloat()
        val I = first + second + third

        if (I.x > 255) {
            I.x = 255f
        }
        if (I.y > 255) {
            I.y = 255f
        }
        if (I.z > 255) {
            I.z = 255f
        }
        return Color(I.x.toInt(), I.y.toInt(), I.z.toInt())
    }
}

data class Light(
    val ambient: Float,
    val diffuse: Float,
    val specular: Float,
    val shininess: Float
)