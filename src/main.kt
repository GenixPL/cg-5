import helpers.Float3
import org.opencv.core.Core
import org.opencv.highgui.HighGui
import java.awt.Color


fun main() {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    val w = 600
    val h = 600

    val ellipsoids = ArrayList<Sphere>()
//    ellipsoids.add(Ellipsiod(Float3(0f, 0f, 0f), 200f, 200f, 200f, Color(255, 255, 255)))
//    ellipsoids.add(Ellipsiod(Float3(0f, 50f, 0f), 25f, 50f, 25f, Color(0, 255, 255)))
//    ellipsoids.add(Ellipsiod(Float3(200f, 200f, 200f), 100f, 100f, 100f, Color(255, 0, 255)))
//    ellipsoids.add(Ellipsiod(Float3(100f, 100f, -100f), 25f, 50f, 25f, Color(0, 255, 0)))
//    ellipsoids.add(Ellipsiod(Float3(30f, -100f, 50f), 25f, 50f, 25f, Color(255, 255, 0)))

    ellipsoids.add(
        Sphere(
            Float3(-10f, 44f, 10f),
            15f,
            Light(0.5f, 1f, 1f, 1f),
            Float3(0f, 0f, 255f)
        )
    )

    ellipsoids.add( //good one
        Sphere(
            Float3(30f, 80f, -40f),
            30f,
            Light(0.05f, 1f, 1f, 4f),
            Float3(0f, 120f, 120f)
        )
    )

    ellipsoids.add(
        Sphere(
            Float3(0f, 20f, 0f),
            10f,
            Light(0.15f, 0.5f, 0.5f, 50f),
            Float3(255f, 120f, 50f)
        )
    )

    ellipsoids.add(
        Sphere(
            Float3(0f, 0f, -50f),
            10f,
            Light(0.2f, 0.5f, 1f, 50f),
            Float3(0f, 250f, 100f)
        )
    )


    val light = PointLight(Float3(200f, 0f, 200f), Float3(255f, 255f, 255f))

    val camera = Camera(w, h)
    val img = Image(camera, w, h)

    var isShowing = true
    while (isShowing) {
        val copy = ArrayList<Sphere>()
        ellipsoids.forEach {
            copy.add(it.copy())
        }
        HighGui.imshow("img", img.getImg(copy, light))

        val key = HighGui.waitKey()

        when (HighGui.pressedKey.toChar()) {
            'B' -> {
                isShowing = false
            }

            'Q' -> {
                camera.position.z -= 5
                println("camera distanceFromCamera decreased")
            }

            'E' -> {
                camera.position.z += 5
                println("camera distanceFromCamera increased")
            }

            'A' -> {
                camera.xRotation -= 5
                println("camera x rotation decreased")
            }

            'D' -> {
                camera.xRotation += 5
                println("camera x rotation increased")
            }

            'W' -> {
                camera.yRotation += 5
                println("camera y rotation increased")
            }

            'S' -> {
                camera.yRotation -= 5
                println("camera y rotation decreased")
            }

            else -> {
                println("other key: ${HighGui.pressedKey.toChar()}")
            }
        }

        camera.updateMatrices()
    }

    System.exit(0)
}