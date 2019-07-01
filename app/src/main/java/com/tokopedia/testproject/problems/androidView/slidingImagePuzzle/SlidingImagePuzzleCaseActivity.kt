
package com.tokopedia.testproject.problems.androidView.slidingImagePuzzle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tokopedia.testproject.R
import com.tokopedia.testproject.fromHtml
import com.tokopedia.testproject.loadFile
import kotlinx.android.synthetic.main.activity_problem_simulation.*

class SlidingImagePuzzleCaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // for example only. You may change this to any image url.
//        val imageUrl = "http://example.com/image.jpg";
//        val imageUrl = "https://dummyimage.com/1600x1600/39bfd1/e4e4ed.jpg";
        val imageUrl = "https://www.gstatic.com/webp/gallery/1.jpg";

        setContentView(R.layout.activity_problem_simulation)
        webView.loadFile("sliding_image.html")
        btn_simulate.setOnClickListener {
            startActivity(SlidingImageGameActivity.getIntent(this, imageUrl))
        }
    }

}
