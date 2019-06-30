import helpers.*
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


class Camera(
    val W: Int,
    val H: Int
) {

    var position = Float3(0f, 0f, 160f)
    var target = Float3(0f, 0f, 0f)
    var fov = 90f
    var near = 1f     //by those we can change size of stuff
    var far = 300f      //by those we can change size of stuff
    lateinit var viewMatrix: RealMatrix
    lateinit var projectionMatrix: RealMatrix

    var xRotation = 0
    var yRotation = 0


    init {
        initViewMatrix()
        initProjectionMatrix()
    }

    private fun initProjectionMatrix() {//
        // General form of the Projection Matrix
        //
        // uh = Cot( fov/2 ) == 1/Tan(fov/2)
        // uw / uh = 1/aspect
        //
        //   uw         0       0       0
        //    0        uh       0       0
        //    0         0      f/(f-n)  1
        //    0         0    -fn/(f-n)  0

        val aspRat = W / H
        val near = this.near
        val far = this.far

        val uh = 1f / tan(radians(fov) * 0.5f)
        val uw = 1f / aspRat

        val frustumDepth = far - near
        val oneOverDepth = 1 / frustumDepth

        val projectionMatrixData = arrayOf(
            doubleArrayOf(uw.toDouble(), 0.0, 0.0, 0.0),
            doubleArrayOf(0.0, uh.toDouble(), 0.0, 0.0),
            doubleArrayOf(0.0, 0.0, (far * oneOverDepth).toDouble(), 1.0),
            doubleArrayOf(0.0, 0.0, (-far * near * oneOverDepth).toDouble(), 0.0)
        )
        projectionMatrix = MatrixUtils.createRealMatrix(projectionMatrixData)
    }

    private fun initViewMatrix() {
        val up = normalize(Float3(0f, 1f, 0f))

        val f = normalize(position.minus(target))
        val r = cross(up, f)
        val u = cross(f, r)

        val viewMatrixData = arrayOf(
            doubleArrayOf(r.x.toDouble(), r.y.toDouble(), r.z.toDouble(), -position.x.toDouble()),
            doubleArrayOf(u.x.toDouble(), u.y.toDouble(), u.z.toDouble(), -position.y.toDouble()),
            doubleArrayOf(f.x.toDouble(), f.y.toDouble(), f.z.toDouble(), -position.z.toDouble()),
            doubleArrayOf(0.0, 0.0, 0.0, 1.0)
        )
        viewMatrix = MatrixUtils.createRealMatrix(viewMatrixData)
    }


    fun updateMatrices() {
        initProjectionMatrix()
        initViewMatrix()
    }

    fun byInvViewMatrix(vec: Float4): Float4 {
        val vData = arrayOf(
            doubleArrayOf(vec.x.toDouble()),
            doubleArrayOf(vec.y.toDouble()),
            doubleArrayOf(vec.z.toDouble()),
            doubleArrayOf(vec.w.toDouble())
        )
        var V = MatrixUtils.createRealMatrix(vData)
        V = MatrixUtils.inverse(viewMatrix).multiply(V)

        val v = Float4(
            V.getEntry(0, 0).toFloat(),
            V.getEntry(1, 0).toFloat(),
            V.getEntry(2, 0).toFloat(),
            V.getEntry(3, 0).toFloat()
        )

        return v
    }

    fun byViewMatrix(vec: Float4): Float4 {
        val vData = arrayOf(
            doubleArrayOf(vec.x.toDouble()),
            doubleArrayOf(vec.y.toDouble()),
            doubleArrayOf(vec.z.toDouble()),
            doubleArrayOf(vec.w.toDouble())
        )
        var V = MatrixUtils.createRealMatrix(vData)
        V = viewMatrix.multiply(V)

        val v = Float4(
            V.getEntry(0, 0).toFloat(),
            V.getEntry(1, 0).toFloat(),
            V.getEntry(2, 0).toFloat(),
            V.getEntry(3, 0).toFloat()
        )

        return v
    }

    fun byProjectionMatrix(vec: Float4): Float4 {
        val vData = arrayOf(
            doubleArrayOf(vec.x.toDouble()),
            doubleArrayOf(vec.y.toDouble()),
            doubleArrayOf(vec.z.toDouble()),
            doubleArrayOf(vec.w.toDouble())
        )
        var V = MatrixUtils.createRealMatrix(vData)
        V = projectionMatrix.multiply(V)

        val v = Float4(
            V.getEntry(0, 0).toFloat(),
            V.getEntry(1, 0).toFloat(),
            V.getEntry(2, 0).toFloat(),
            V.getEntry(3, 0).toFloat()
        )

        return v
    }

    fun byInvProjectionMatrix(vec: Float4): Float4 {
        val vData = arrayOf(
            doubleArrayOf(vec.x.toDouble()),
            doubleArrayOf(vec.y.toDouble()),
            doubleArrayOf(vec.z.toDouble()),
            doubleArrayOf(vec.w.toDouble())
        )
        var V = MatrixUtils.createRealMatrix(vData)
        V = MatrixUtils.inverse(projectionMatrix).multiply(V)

        val v = Float4(
            V.getEntry(0, 0).toFloat(),
            V.getEntry(1, 0).toFloat(),
            V.getEntry(2, 0).toFloat(),
            V.getEntry(3, 0).toFloat()
        )

        return v
    }

    fun rotateByX(vec: Float3): Float3 {
        val angle = radians(xRotation.toFloat())
        val s = sin(angle)
        val c = cos(angle)

        val rxData = arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0),
            doubleArrayOf(0.0, c.toDouble(), -s.toDouble()),
            doubleArrayOf(0.0, s.toDouble(), c.toDouble())
        )
        val RX = MatrixUtils.createRealMatrix(rxData)

        val vData = arrayOf(
            doubleArrayOf(vec.x.toDouble()),
            doubleArrayOf(vec.y.toDouble()),
            doubleArrayOf(vec.z.toDouble())
        )
        var V = MatrixUtils.createRealMatrix(vData)
        V = RX.multiply(V)

        val v = Float3(
            V.getEntry(0, 0).toFloat(),
            V.getEntry(1, 0).toFloat(),
            V.getEntry(2, 0).toFloat()
        )

        return v
    }

    fun rotateByY(vec: Float3): Float3 {
        val angle = radians(yRotation.toFloat())
        val s = sin(angle)
        val c = cos(angle)

        val ryData = arrayOf(
            doubleArrayOf(c.toDouble(), 0.0, -s.toDouble()),
            doubleArrayOf(0.0, 1.0, 0.0),
            doubleArrayOf(s.toDouble(), 0.0, c.toDouble())
        )
        val RY = MatrixUtils.createRealMatrix(ryData)

        val vData = arrayOf(
            doubleArrayOf(vec.x.toDouble()),
            doubleArrayOf(vec.y.toDouble()),
            doubleArrayOf(vec.z.toDouble())
        )
        var V = MatrixUtils.createRealMatrix(vData)
        V = RY.multiply(V)

        val v = Float3(
            V.getEntry(0, 0).toFloat(),
            V.getEntry(1, 0).toFloat(),
            V.getEntry(2, 0).toFloat()
        )

        return v
    }
}