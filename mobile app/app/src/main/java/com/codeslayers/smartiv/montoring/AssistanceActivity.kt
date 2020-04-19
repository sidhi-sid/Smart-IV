package com.codeslayers.smartiv.montoring

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.codeslayers.smartiv.R
import com.codeslayers.smartiv.model.DripDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_assistance.*

class AssistanceActivity : AppCompatActivity() {

    val list = ArrayList<DripDetails>()
    private val mAdapter = AssistanceAdapter(list)

    private val dripDB by lazy {
        FirebaseDatabase.getInstance("https://smart-iv-hackon.firebaseio.com/")
    }

    private val currentUser by lazy {
        FirebaseAuth.getInstance().currentUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assistance)

        val patDetails = dripDB.reference

        patDetails.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                list.clear()
                val roomNumbers = p0.children.iterator()
                while (roomNumbers.hasNext()) {
                    val roomNumber = roomNumbers.next()
                    val bedNumbers = roomNumber.children.iterator()
                    while (bedNumbers.hasNext()) {
                        val bedNumber = bedNumbers.next()
                        if (bedNumber.child("nurseID").value.toString() == currentUser?.email) {
                            list.add(
                                DripDetails(
                                    roomNumber.key.toString().substring(12, 14),
                                    bedNumber.key.toString().substring(11),
                                    bedNumber.child("consultingDoctor").value.toString(),
                                    bedNumber.child("dripStatus").value.toString().toBoolean(),
                                    bedNumber.child("nurseID").value.toString(),
                                    bedNumber.child("patientBloodGroup").value.toString(),
                                    bedNumber.child("patientID").value.toString(),
                                    bedNumber.child("patientIVFluid").value.toString(),
                                    bedNumber.child("patientName").value.toString(),
                                    bedNumber.child("patientSymptoms").value.toString()
                                )
                            )
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })

        rvMontering.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMontering.adapter = mAdapter

    }
}
