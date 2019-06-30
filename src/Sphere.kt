import helpers.*
import org.apache.commons.math3.linear.MatrixUtils
import java.awt.Color
import kotlin.math.sqrt


data class Sphere(
    var center: Float3,
    val radius: Float,
    val light: Light,
    val color: Float3,
    var distanceFromCamera: Float = 0f
) {

    fun isIntersecting(ray: Ray): IntersectionResult {
        val t = dot(center.minus(ray.origin), ray.direction)
        val p = ray.origin.plus(ray.direction.times(t))
        val y = length(center.minus(p))

        if (y < radius) {
            val x = sqrt(radius * radius - y * y)
            val t1 = t - x
            val t2 = t + x

            if (t1 < t2) {
                val pt = pointAt(ray, t1)
                val normData = arrayOf(
                    doubleArrayOf(pt.x.toDouble()),
                    doubleArrayOf(pt.y.toDouble()),
                    doubleArrayOf(pt.z.toDouble())
                )
                val NORM = MatrixUtils.createRealMatrix(normData)
                val normal = pt.minus(center) / NORM.norm.toFloat()

                return IntersectionResult(true, t1, normal, pt)

            } else {
                val pt = pointAt(ray, t2)
                val normData = arrayOf(
                    doubleArrayOf(pt.x.toDouble()),
                    doubleArrayOf(pt.y.toDouble()),
                    doubleArrayOf(pt.z.toDouble())
                )
                val NORM = MatrixUtils.createRealMatrix(normData)
                val normal = pt.minus(center) / NORM.norm.toFloat()

                return IntersectionResult(true, t2, normal, pt)
            }

        } else {
            return IntersectionResult(false, -1f, Float3(), Float3())
        }
    }

    fun setDistance(cameraPos: Float3) {
        val d =
            sqrt(pow(cameraPos.x - center.x, 2f) + pow(cameraPos.y - center.y, 2f) + pow(cameraPos.z - center.z, 2f))

        distanceFromCamera = d
    }

}

data class IntersectionResult(
    val isIntersecting: Boolean,
    val t: Float,
    val normalVector: Float3,
    val pt: Float3
)