package com.example.safetrace

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safetrace.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var binding: FragmentHomeBinding
    lateinit var inviteAdapter: Invite_Adapter
    //here we created a list of contacts which is a list of ContactModel ,it's a list of data class ContactModel which contains the name and number of the contact ,it's type is ArrayList
    private val listContacts: ArrayList<ContactModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//4 here we created the list of memberModel and passed it to the adapter..........................................................................................
        //here we created a memberModel which is a data class that
        // contains the image and name of the member
        val listMember = listOf<memberModel>(
//            here we passed the image and name of the member
            memberModel(
                name = "Sagar Moktan",
                image = R.drawable.ic_user
            ),
            memberModel(
                name = "rajendra",
                image = R.drawable.ic_user
            ),
            memberModel(
                name = "yogendra",
                image = R.drawable.ic_user
            ),
            memberModel(
                name = "yogendra",
                image = R.drawable.ic_user
            )
        )

        Log.d("fetchContact", "onViewCreated: Member list initialized with ${listMember.size} members")
//1 here listContact is empty and we are passing it to the adapter
         inviteAdapter = Invite_Adapter(listContacts)
        Log.d("fetchContact", "onViewCreated: Invite adapter initialized,size = ${listContacts.size}")


  fetchDatabaseContacts()
//        Here we used coroutine to fetch the contacts from the phone ,coroutines helps us to run the code in the background thread and not in the main thread
        CoroutineScope(Dispatchers.IO).launch {
          val contactsFromPhone = fetchContacts()
            Log.d("fetchContact", "Coroutine: Contacts fetched from device: ${contactsFromPhone.size}")
            insertDatabaseContacts(contactsFromPhone)
            Log.d("fetchContact", "Coroutine: Contacts inserted into Room database")

        }


        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        inviteRecycler.adapter = inviteAdapter

        //5  here we created the adapter and passed the list of memberModel to it ..................................................................................
        val adapter = MemberAdapter(listMember)
        //requireView() cause we don't have a context here
        val recycler = requireView().findViewById<RecyclerView>(R.id.recyclerview)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter


        val threeDots = requireView().findViewById<ImageView>(R.id.ic_threedots)
        threeDots.setOnClickListener {
            sharedPref.putBoolean(prefConstants.IS_USER_LOGGED_IN, false)
            FirebaseAuth.getInstance().signOut()
        }
    }



    private  fun fetchDatabaseContacts()  {
        val database = MyFamilyDatabase.getDatabase(requireContext())
         database.contactDao().getAllContacts().observe(viewLifecycleOwner){
             listContacts.clear()
             Log.d("fetchContact", "fetchDatabaseContacts: Updated list from database, size = ${listContacts.size}")
             listContacts.addAll(it)
             Log.d("fetchContact", "fetchDatabaseContacts: Updated list from database, size = ${listContacts.size}")

             inviteAdapter.notifyDataSetChanged()
         }
    }


    private suspend fun insertDatabaseContacts(listContacts: ArrayList<ContactModel>) {
        val database = MyFamilyDatabase.getDatabase(requireContext())
        database.contactDao().insertAll(listContacts)
    }


    @SuppressLint("Range", "NewApi")
    private fun fetchContacts(): ArrayList<ContactModel> {

//        we used requireActivity() to get the context of the fragment but if we were already in an activity instead of fragment we can use requireContext().
//        contentResolver is used to access the contacts of the phone and query is used to query the contacts of the phone
        val cr = requireActivity().contentResolver
//          cr is an instance of ContentResolver which is used to access the contacts of the phone
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
// cursor here has a refrence or pointer to all the contacts of the phone
        val lisContacts: ArrayList<ContactModel> = ArrayList()

        if (cursor != null && cursor.count > 0) {
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
//                here id is the id of the contact and we are getting it from the cursor
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
//                here hasPhoneNumber is the number of the contact and we are getting it from the cursor it can be 1 or 0 ,1 means it has a phone number and 0 means it doesn't have a phone number and we are getting it from the cursor .


                if (hasPhoneNumber > 0) {
                    val pcur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null

// here pcur is the cursor which has the phone number of the contact and we are getting it from the cursor i.e pcur iterates through the contacts that have phone numbers and we are getting it from the cursor.
                    )
                    if (pcur != null && pcur.count > 0) {
                        while (pcur != null && pcur.moveToNext()) {
                            val phoneNum =
                                pcur.getString(pcur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            lisContacts.add(ContactModel(name, phoneNum))
                        }
                        pcur.close()
                    }
                    if (pcur != null) {
                        pcur.close()
                    }
                }
            }
        }

        return lisContacts
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()

    }
}