package com.nex.roomdemo

import android.util.Patterns
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.nex.roomdemo.db.Subscriber
import com.nex.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern

class SubscriberViewModel(private val subscriberRepository: SubscriberRepository) : ViewModel() {

    private var isUpdateOrDelete=false

    private lateinit var subscriberToUpdateOrDelete:Subscriber

    val inputName = MutableLiveData<String?>()

    val inputEmail = MutableLiveData<String?>()

    val saveOrUpdateBtnText = MutableLiveData<String>()

    val clearAllOrDeleteBtnText = MutableLiveData<String>()

    val subscribers=subscriberRepository.subscriber

    private val statusMessage =MutableLiveData<Event<String>>()

   val message: LiveData<Event<String>>
    get()=statusMessage
    init {
        saveOrUpdateBtnText.value = "Save"
        clearAllOrDeleteBtnText.value = "Clear All"
    }


    fun saveOrUpdate(){
        if(inputName.value==null){
            statusMessage.value= Event("Please enter Subscriber's name")
        }else if(inputEmail.value==null){
            statusMessage.value= Event("Please enter Subscriber's email")
        }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter Correct Email address")
        }else{
            if(isUpdateOrDelete){
                subscriberToUpdateOrDelete.name=inputName.value!!
                subscriberToUpdateOrDelete.email=inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            }else{
                val name =inputName.value!!
                val email=inputEmail.value!!
                insert(Subscriber(0,name, email))
                inputName.value=null
                inputEmail.value=null
            }
        }




    }

    fun clearAllOrUpdate(){
        if(isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        }else{
            deleteAll()
        }

    }

    fun intiUpdateOrDelete(subscriber: Subscriber){
        inputName.value=subscriber.name
        inputEmail.value=subscriber.email
        isUpdateOrDelete=true
        subscriberToUpdateOrDelete=subscriber
        saveOrUpdateBtnText.value = "Update"
        clearAllOrDeleteBtnText.value = "Delete"
    }

    fun insert(subscriber: Subscriber):Job=viewModelScope.launch {
       val rowId = subscriberRepository.insert(subscriber)
        if(rowId>-1){
            statusMessage.value= Event("Subscriber Inserted Successfully $rowId")
        }else{
            statusMessage.value= Event("Error Occurred")
        }

    }

    fun update(subscriber: Subscriber):Job=viewModelScope.launch {
        val rowId =  subscriberRepository.update(subscriber)
        if(rowId>0){
            inputName.value=null
            inputEmail.value=null
            isUpdateOrDelete=false
            saveOrUpdateBtnText.value = "Save"
            clearAllOrDeleteBtnText.value = "Clear All"
            statusMessage.value= Event("$rowId Row Updated Successfully")
        }else{
            statusMessage.value= Event("Error Occurred")
        }


    }

    fun delete(subscriber: Subscriber):Job=viewModelScope.launch {
        val rowId =   subscriberRepository.delete(subscriber)
        if(rowId > 0) {
            statusMessage.value = Event("$rowId Row Deleted Successfully")
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateBtnText.value = "Save"
            clearAllOrDeleteBtnText.value = "Clear All"
        }else{
            statusMessage.value= Event("Error Occurred")
        }
    }

    fun deleteAll():Job=viewModelScope.launch {
        val rowId =  subscriberRepository.deleteAll()
        if(rowId > 0) {
            statusMessage.value = Event("All Subscriber Deleted Successfully")
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateBtnText.value = "Save"
            clearAllOrDeleteBtnText.value = "Clear All"
        }else{
            statusMessage.value= Event("Error Occurred")
        }
    }
}