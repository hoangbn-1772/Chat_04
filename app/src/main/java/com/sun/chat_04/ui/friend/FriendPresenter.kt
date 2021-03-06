package com.sun.chat_04.ui.friend

import com.sun.chat_04.data.model.Friend
import com.sun.chat_04.data.repositories.FriendRepository
import com.sun.chat_04.ui.signup.RemoteCallback
import com.sun.chat_04.util.Global

class FriendPresenter(
    private val view: FriendContract.View,
    private val repository: FriendRepository
) : FriendContract.Presenter {

    override fun getFriends() {
        val userId = Global.firebaseAuth.currentUser?.uid
        repository.getFriends(userId, object : RemoteCallback<ArrayList<Friend>> {
            override fun onSuccessfuly(data: ArrayList<Friend>) {
                if (data.isEmpty()) {
                    view.showEmptyData()
                } else {
                    view.onGetFriendsSuccessfully(data)
                }
            }

            override fun onFailure(exception: Exception?) {
                view.onGetFriendsFailed(exception)
            }
        })
    }
}
