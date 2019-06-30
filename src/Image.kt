import helpers.Float3
import helpers.Float4
import helpers.normalize
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.awt.Color


class Image(
    val camera: Camera,
    val W: Int,
    val H: Int
) {

    private val TAG = this::class.simpleName


    fun getImg(ellipsoids: ArrayList<Sphere>, light: PointLight): Mat {
        val img = Mat.zeros(W, H, CvType.CV_8UC3)

        ellipsoids.forEach {
            it.center = camera.rotateByX(it.center)
            it.center = camera.rotateByY(it.center)
            it.setDistance(camera.position)
        }

        ellipsoids.sortBy { it.distanceFromCamera }

        /* TESTING RAYS */
//        for (x in 0 until W) {
//            val r = getRay(x, 300)
//            println("o:${r.origin.x} ${r.origin.y} ${r.origin.z} \t d:${r.direction.x} ${r.direction.y} ${r.direction.z}")
//        }

        /* TESTING COLLISION */
//        val e = ellipsoids[0]
//        val r1 = getRay(299, 300)
//        val r2 = getRay(300, 300)
//        val r3 = getRay(301, 300)
//
//        println(e.isIntersecting(r1))
//        println(e.isIntersecting(r2))
//        println(e.isIntersecting(r3))


        for (x in 0 until W) {
            for (y in 0 until H) {
                val ray = getRay(x, y)

                ellipsoids.forEach { ellipsoid ->
                    val result = ellipsoid.isIntersecting(ray)

                    if (result.isIntersecting) {
                        if (!isColored(img, x, y)) {
                            val color = light.getColor(result.normalVector, result.pt, camera.position, ellipsoid.light, Float3(ellipsoid.color))
                            setPixel(img, x, y, color)
                        }
                    }

                }
            }
        }

        return img
    }

    private fun isColored(m: Mat, x: Int, y: Int): Boolean {
        val color = m.get(x, y)

        if (color[0] != 0.0 || color[1] != 0.0 || color[2] != 0.0) {
            return true
        } else {
            return false
        }
    }

    private fun setPixel(m: Mat, x: Int, y: Int, color: Color) {
        val r = color.red.toDouble()
        val g = color.green.toDouble()
        val b = color.blue.toDouble()

        m.put(x, y, b, g, r)
    }

    private fun getRay(x: Int, y: Int): Ray {
        val origin = Float3(camera.position.x, camera.position.y, camera.position.z)

        val x = 2f * x / W - 1f
        val y = 1f - 2f * y / H
        val z = 1f
        val ray_nds = Float3(x, y, z)

        val ray_clip = Float4(ray_nds.x, ray_nds.y, -1f, 1f)

        var ray_eye = camera.byInvProjectionMatrix(ray_clip)
        ray_eye = Float4(ray_eye.x, ray_eye.y, -1f, 0f)

        var temp = camera.byInvViewMatrix(ray_eye)
        var ray_wor = Float3(temp.x, temp.y, temp.z)
        ray_wor = normalize(ray_wor)

        return Ray(origin, ray_wor)
    }

}

