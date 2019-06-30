import helpers.Float3
import helpers.Float4
import helpers.pow
import org.apache.commons.math3.linear.MatrixUtils
import java.awt.Color
import kotlin.math.sqrt


class Ellipsiod(
    var center: Float3,
    val a: Float,
    val b: Float,
    val c: Float,
    val color: Color
) {

    private val TAG = this::class.simpleName


    fun isIntersecting(ray: Ray): Float {
        val aData = arrayOf(
            doubleArrayOf(pow(a, -2f).toDouble(), 0.0, 0.0),
            doubleArrayOf(0.0, pow(b, -2f).toDouble(), 0.0),
            doubleArrayOf(0.0, 0.0, pow(c, -2f).toDouble())
        )
        val A = MatrixUtils.createRealMatrix(aData)

        val dData = arrayOf(
            doubleArrayOf(ray.direction.x.toDouble()),
            doubleArrayOf(ray.direction.y.toDouble()),
            doubleArrayOf(ray.direction.z.toDouble())
        )
        val D = MatrixUtils.createRealMatrix(dData)
        val DT = D.transpose()

        val oc = ray.origin.minus(center)
        val ocData = arrayOf(
            doubleArrayOf(oc.x.toDouble()),
            doubleArrayOf(oc.y.toDouble()),
            doubleArrayOf(oc.z.toDouble())
        )
        val OC = MatrixUtils.createRealMatrix(ocData)
        val OCT = OC.transpose()

        val a = DT.multiply(A).multiply(D).getEntry(0, 0)
        val b = OCT.multiply(A).multiply(D).getEntry(0, 0) + DT.multiply(A).multiply(OC).getEntry(0, 0)
        val c = OCT.multiply(A).multiply(OC).getEntry(0, 0)

        var delta = pow(b.toFloat(), 2f) - (4 * a * c)
        if (delta < 0) {
            return -1f

        } else {
            delta = sqrt(delta)
            val t1 = (-b - delta) / (2 * a)
            val t2 = (-b + delta) / (2 * a)

            if (t1 < t2) {
                return 2f//t1.toFloat()
            } else {
                return 2f //t2.toFloat()
            }
        }
    }
}


