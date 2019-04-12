package com.sun.chat_04.data.repositories

import com.sun.chat_04.data.model.User
import com.sun.chat_04.ui.signup.RemoteCallback

interface FriendRequestDataSource {
    interface Remote {
        fun approveFriendRequest( user: User, callback: RemoteCallback<Boolean>)
        fun cancelFriendRequest(user: User, callback: RemoteCallback<Boolean>)
        fun showFriendRequests(callback: RemoteCallback<ArrayList<User>>)
    }
}
