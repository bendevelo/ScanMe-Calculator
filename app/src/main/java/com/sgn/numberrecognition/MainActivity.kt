package com.sgn.numberrecognition

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.sgn.numberrecognition.databinding.ActivityMainBinding


private var imageBitmap: Bitmap? = null

@SuppressLint("StaticFieldLeak")
lateinit var binding: ActivityMainBinding

 var kode=0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //input button from get image options
        binding.input.setOnClickListener({

            val dialog = BottomSheetDialog(this)

            val view = layoutInflater.inflate(R.layout.bottomsheetinput, null)
            val Photofile = view.findViewById<ImageView>(R.id.photofile)
            val openkamera = view.findViewById<ImageView>(R.id.TakePicture)

            val linefile = view.findViewById<View>(R.id.linefile)
            val lineCamera = view.findViewById<LinearLayout>(R.id.lincamera)



            if(resources.getString(R.string.feature).equals("camera")){
                linefile.setVisibility(View.GONE)

            }


            //pick image from live camera
            openkamera.setOnClickListener({
                kode =1
                getImageFromCamera()
                dialog.dismiss()

            })

            // pick image from gallery
            Photofile.setOnClickListener({
                kode=2
                getImageFromGallery()
                dialog.dismiss()
            })


            dialog.setCancelable(true)

            dialog.setContentView(view)
            dialog.show()

        })

        //set image preview on screen
        binding.imageView.setOnClickListener({

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.scanme)
            dialog.window?.setBackgroundDrawableResource(R.drawable.bgscanme)
            dialog.show()
        })


    }


    fun getImageFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(takePictureIntent)

    }

    fun getImageFromGallery() {
        val takePictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(takePictureIntent)

    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                if(kode==1){
                val data2: Intent? = result.data

                val extra: Bundle? = data2?.extras
                imageBitmap = extra?.get("data") as Bitmap?
                    binding.image.setImageBitmap(imageBitmap)

                imageBitmap?.let { detectText(it) }

                }else{
                    val data = result.data
                    val imgUri = data?.data

                    binding.image.setImageURI(imgUri)
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(
                        imgUri.toString()
                    ))
                    detectText(bitmap)

                }


            }
        }

    fun detectText(imageView: Bitmap) {

        val image = FirebaseVisionImage.fromBitmap(imageView)
        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().onDeviceTextRecognizer

        detector.processImage(image).addOnSuccessListener(OnSuccessListener {
            process(it)

        }).addOnFailureListener({

        })
    }

    fun process(text: FirebaseVisionText) {
        val blocks: List<FirebaseVisionText.TextBlock> = text.textBlocks

        if (blocks.size == 0) {

            Toast.makeText(applicationContext, "No Text ", Toast.LENGTH_LONG).show();
            return;
        }

        for (block in blocks) {

            val txt: String = block.getText()

            hitung(txt)
        }

    }


    // method for aritmatic operation
    fun hitung(bil: String) {

        val loading = Loading(this)
        loading.startDialogLoading()

        val bil2: String = bil.replace(" ", "")
        val bilArray = CharArray(bil2.length)

        for (i in 0 until bil2.length) {
            bilArray[i] = bil2[i]
        }

        var operator = 'b'
        var index = 0

        for (i in 0 until bilArray.size) {
            if (bilArray[i] == '+' || bilArray[i] == '-' || bilArray[i] == 'x'|| bilArray[i] == '/') {
                operator = bilArray[i]
                index = i
                break
            }
        }

        val num1 = StringBuffer()
        val num2 = StringBuffer()
        val num3 = StringBuffer()

        for (i in 0 until index) {
            num1.append(bilArray[i])
        }

        for (i in index + 1 until bilArray.size) {
            num2.append(bilArray[i])
        }


        for (i in 0 until num2.length) {
            if (num2[i] == '+' || num2[i] == '-' || num2[i] == 'x') {
                break
            } else {
                num3.append(num2[i])
            }
        }



        if (num1.toString().isEmpty() || num3.toString().isEmpty()) {
            Toast.makeText(this, "Tidak memenuhi syarat perhitungan", Toast.LENGTH_LONG).show()
        } else {


            //show result aritmatic operation

            try {
                var hasil = 0
                if (operator == '+') {
                    hasil = num1.toString().toInt() + num3.toString().toInt()
                } else if (operator == '-') {
                    hasil = num1.toString().toInt() - num3.toString().toInt()
                } else if (operator == 'x') {
                    hasil = num1.toString().toInt() * num3.toString().toInt()
                }else if (operator == '/') {
                    hasil = num1.toString().toInt() / num3.toString().toInt()
                }


                binding.textView4.text = (hasil).toString()
                binding.textView5.text = "( $num1 $operator $num3 )"
            }catch (e: NumberFormatException) {

                val dialog = Dialog(this)
                dialog.setContentView(R.layout.erroralert)
                dialog.window?.setBackgroundDrawableResource(R.drawable.bgscanme)
                dialog.show()

            }finally {

                loading.dismissDialog()

            }


        }


    }
}