package com.example.safetrace

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safetrace.databinding.FragmentGuardBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class guardFragment : Fragment(), InviteMailAdapter.OnActionClick {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
   lateinit var binding: FragmentGuardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGuardBinding.inflate(inflater, container, false)



        // Inflate the layout for this fragment
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendInvite.setOnClickListener {
            sendInvite()
        }
        getInvites()
    }

    private fun getInvites() {
        val firestore = Firebase.firestore
        firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .collection("invites").get().addOnCompleteListener{
                if(it.isSuccessful){
                    val list:ArrayList<String> = ArrayList()
                    for (item in it.result){
                        if (item.get("invite_status")==0L){
                            list.add(item.id)
                        }
                    }
                    val adapter = InviteMailAdapter(list,this)
                    binding.inviteRecycler.adapter = adapter

                }
            }
    }

    private fun sendInvite() {
        val mail = binding.inviteMail.text.toString()//target mail
        val firestore = Firebase.firestore
        val data = hashMapOf("invite_status" to 0)
        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()//sender mail

        firestore.collection("users")
            .document(mail).collection("invites")
            .document(senderMail)
            .set(data).addOnSuccessListener {


            }.addOnFailureListener {

            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = guardFragment()
    }

    override fun onAcceptClick(mail: String) {
        val firestore = Firebase.firestore
        val data = hashMapOf("invite_status" to 1)
        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()//sender mail

        firestore.collection("users")
            .document(senderMail).collection("invites")
            .document(mail)
            .set(data).addOnSuccessListener {


            }.addOnFailureListener {

            }
    }

    override fun onDenyClick(mail: String) {
        val firestore = Firebase.firestore
        val data = hashMapOf("invite_status" to -1)
        val senderMail = FirebaseAuth.getInstance().currentUser?.email.toString()//sender mail

        firestore.collection("users")
            .document(senderMail).collection("invites")
            .document(mail)
            .set(data).addOnSuccessListener {


            }.addOnFailureListener {

            }
    }
}