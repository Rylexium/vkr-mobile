package com.example.vkr.activity.maps

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.vkr.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class YandexMapsActivity : AppCompatActivity() {

    private lateinit var mapview : MapView
    private val MAPKIT_API_KEY = "c516b242-0d62-453a-8e44-2c9002475cba"

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        setContentView(R.layout.yandex_maps_activity)
        super.onCreate(savedInstanceState)

        mapview = findViewById(R.id.mapView)
        mapview.map.isRotateGesturesEnabled = false
        mapview.map.move(
            CameraPosition(Point(53.213779, 50.176837), 16f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0F),
            null)
        mapview.map.mapObjects.addPlacemark(Point(53.213779, 50.176837), ImageProvider.fromBitmap(drawSimpleBitmap("Приёмная комиссия")))

        val mapKit = MapKitFactory.getInstance()
        val userLocationLayer = mapKit.createUserLocationLayer(mapview.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
    }

    override fun onStart() {
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStart()
        super.onStop()
    }
    private fun drawSimpleBitmap(text : String) : Bitmap {
        val picSize = 50
        val bitmap = Bitmap.createBitmap(picSize, picSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        canvas.drawCircle((picSize / 2).toFloat(), (picSize / 2).toFloat(), (picSize / 2).toFloat(), paint)

        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.textSize = 20f
        paint.textAlign = Paint.Align.CENTER;
        canvas.drawText(text, (picSize / 2).toFloat(), picSize / 2 - ((paint.descent() + paint.ascent()) / 2), paint);
        return bitmap;
    }
}