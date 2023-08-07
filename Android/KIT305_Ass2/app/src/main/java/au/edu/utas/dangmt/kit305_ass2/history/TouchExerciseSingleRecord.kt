package au.edu.utas.dangmt.kit305_ass2.history

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.ButtonPressedRecord
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseRecord
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseSingleRecordBinding
import au.edu.utas.dangmt.kit305_ass2.databinding.ButtonPressedItemBinding
import au.edu.utas.dangmt.kit305_ass2.exercises.FIREBASE_TAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_IMAGE_CAPTURE = 1

class TouchExerciseSingleRecord : AppCompatActivity() {

    private lateinit var ui : ActivityTouchExerciseSingleRecordBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityTouchExerciseSingleRecordBinding.inflate(layoutInflater)
        setContentView(ui.root)

        // Back button
        ui.backBtn2.setOnClickListener {
            val intent = Intent(this, TouchExerciseHistory::class.java)
            startActivity(intent)
        }

        // Menu button
        ui.menuBtn.setOnClickListener {
            if(ui.exportButton.visibility == View.GONE) {
                ui.exportButton.visibility = View.VISIBLE
                ui.addImageButton.visibility = View.VISIBLE
                ui.deleteButton.visibility = View.VISIBLE
            }
            else {
                ui.exportButton.visibility = View.GONE
                ui.addImageButton.visibility = View.GONE
                ui.deleteButton.visibility = View.GONE
            }
        }

        /** Get record from record index (ID) from History screen **/
        val recordID = intent.getIntExtra(RECORD_INDEX, -1)
        val record = recordList[recordID]

        /** Display record details on screen **/
        val endTimeText = if(record.endTime == null) "-" else record.endTime
        val modeText = when(record.exerciseMode) {
            getString(R.string.exercise_mode_freeplay_key) -> "Free-play mode"
            getString(R.string.exercise_mode_goal_key) -> {
                when(record.goalMode) {
                    getString(R.string.goal_mode_repetition_key) -> "Goal (repetition)"
                    getString(R.string.goal_mode_time_limit_key) -> "Goal (time limit)"
                    else -> "ERROR_2"
                }
            }
            else -> "ERROR_1"
        }
        val statusText = when(record.isCompleted) {
            true -> "Completed"
            false -> "Incomplete"
            else -> "-"
        }

        ui.startTimeTxt.text = record.startTime
        ui.endTimeTxt.text = endTimeText
        ui.modeTxt.text = modeText
        ui.statusTxt.text = statusText
        ui.nRepsTxt.text = record.nReps.toString()
        ui.nButtonsTxt.text = record.nButtons.toString()
        ui.randomOrderTxt.text = if(record.randomOrder == true) "Yes" else "No"
        ui.buttonIndicationTxt.text = if(record.nextButtonIndication == true) "Yes" else "No"
        ui.buttonSizeTxt.text = record.buttonSize.toString()


        // Setup adapter and layout manager for RecyclerView
        Log.d("SIZE_LIST", record.buttonPressedList!!.size.toString())
        if(record.buttonPressedList != null && record.buttonPressedList!!.size != 0) {
            ui.buttonPressedList.adapter = ButtonRecordAdapter(buttonRecords = record.buttonPressedList as MutableList<ButtonPressedRecord>)
            ui.buttonPressedList.layoutManager = LinearLayoutManager(this)
        }
        else {
            // display text when no buttons were pressed
            ui.buttonListHeadingSection.visibility = View.INVISIBLE
            ui.buttonListHeading.text = "No buttons were pressed"
        }

        //get db connection
        val db = Firebase.firestore
        var recordsCollection = db.collection("history")

        // TODO: Export button
        ui.exportButton.setOnClickListener {
            val randomOrderText = if(record.randomOrder == true) "Yes" else "No"
            val nextButtonIndicationText = if(record.nextButtonIndication == true) "Yes" else "No"
            val textToShare =   "${record.startTime}," +
                                "${record.endTime}," +
                                "$modeText," +
                                "$statusText," +
                                "${record.nReps}," +
                                "${record.nButtons}," +
                                "$randomOrderText," +
                                "$nextButtonIndicationText," +
                                "${record.buttonSize}"

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }

        // TODO: Add image button
        ui.addImageButton.setOnClickListener {
            requestToTakeAPicture()
        }


        // TODO: Delete button
        ui.deleteButton.setOnClickListener {
            /** Delete Confirmation **/
            var builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            builder.setTitle(R.string.delete_dialog_title)
            builder.setMessage(R.string.delete_dialog_message)
            builder.setPositiveButton("Delete", DialogInterface.OnClickListener {dialog, id ->
                Log.d("DELETE_BTN", "Confirm deletion")
                Log.d("DELETE_BTN", record.id.toString())
                recordsCollection.document(record.id!!)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Successfully delete Touch history record with id: ${record.id}")
                    }
                    .addOnFailureListener {
                            e -> Log.w(FIREBASE_TAG, "Error deleting document with id: ${record.id}", e)
                    }
                // Delete from recordList (so the History screen updates immediately)
                recordList.removeAll { x -> x.id == record.id }

                // Go back to History screen (all records)
                finish()
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener {dialog, id ->
                dialog.cancel()
            })

            var alert : AlertDialog = builder.create()
            alert.show()
        }

    }

    inner class ButtonRecordHolder(var ui : ButtonPressedItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class ButtonRecordAdapter(private val buttonRecords : MutableList<ButtonPressedRecord>) : RecyclerView.Adapter<ButtonRecordHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonRecordHolder {
            var ui = ButtonPressedItemBinding.inflate(layoutInflater, parent, false)
            return ButtonRecordHolder(ui)
        }

        override fun onBindViewHolder(holder: ButtonRecordHolder, position: Int) {
            val buttonRecord = buttonRecords[position]
            holder.ui.timeText.text = buttonRecord.time
            holder.ui.buttonPressedText.text = buttonRecord.buttonPressed.toString()
        }

        override fun getItemCount(): Int {
            return buttonRecords.size
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestToTakeAPicture()
    {
        getPermissionResult.launch(Manifest.permission.CAMERA)
    }

    private val getPermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        result : Boolean ->
            // Permission is granted
            if (result) {
                takeAPicture()
            }
            // Permission is denied
            else {
                Toast.makeText(this,
                    "Cannot access camera, permission denied",
                    Toast.LENGTH_LONG).show()
            }
        }

    private fun takeAPicture() {
        val photoFile: File = createImageFile()
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "au.edu.utas.dangmt.kit305_ass2",
            photoFile
        )
        getCameraResult.launch(photoURI)
        // launch() opens camera app and tells the camera app te file path to output to
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", // prefix
            ".jpg",     // suffix
            storageDir      // directory
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private val getCameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
                result: Boolean ->
            //step 7, part 1
            if (result) {
                setPic(ui.imageView)
            }
        }

    // Read image that was saved in currentPhotoPath and put into imageView (in ui)
    /** The image is stored in the file at full size
     *      setPic() resizes te image to match ui.imageView's dimensions (for performance + smooth scaling)
    **/
    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this) /** Decode the image **/

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)    /** Set imageView in ui **/
        }
    }

}