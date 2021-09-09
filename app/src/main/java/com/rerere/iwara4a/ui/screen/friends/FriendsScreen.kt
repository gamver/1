package com.rerere.iwara4a.ui.screen.friends

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.friends.Friend
import com.rerere.iwara4a.model.friends.FriendStatus
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.noRippleClickable

@Composable
fun FriendsScreen(friendsViewModel: FriendsViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            FullScreenTopBar(
                title = {
                    Text(text = "好友")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            FriendsList(friendsViewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FriendsList(
    friendsViewModel: FriendsViewModel
) {
    val friendList by friendsViewModel.friendList.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = friendList is DataState.Loading
    )
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            friendsViewModel.loadFriendList()
        }
    ) {
        when (friendList) {
            is DataState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            friendsViewModel.loadFriendList()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "加载失败，点击重试", fontSize = 20.sp)
                }
            }
            else -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    friendList.readSafely()?.takeIf { it.isEmpty() }?.let { 
                        item {
                            Text(text = "没有任何好友", modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center)
                        }
                    }
                    friendList.readSafely()?.groupBy { it.friendStatus }?.forEach { (status, list) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val header =  when (status) {
                                    FriendStatus.PENDING -> "等待同意"
                                    FriendStatus.ACCEPTED -> "已同意"
                                    else -> "未知"
                                }
                                Text(
                                    text = header,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.primary
                                )
                            }
                        }

                        items(list) {
                            FriendItem(friendsViewModel, it)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FriendItem(
    friendsViewModel: FriendsViewModel,
    friend: Friend
) {
    val navController = LocalNavController.current
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("user/${friend.userId}")
            }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = friend.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = friend.date)
            }

            when (friend.friendStatus) {
                FriendStatus.PENDING -> {
                    IconButton(onClick = {
                        friendsViewModel.handleFriendRequest(friend.frId, true){
                            friendsViewModel.loadFriendList()
                        }
                    }) {
                        Icon(Icons.Default.Check, null)
                    }
                    IconButton(onClick = {
                        friendsViewModel.handleFriendRequest(friend.frId, false){
                            friendsViewModel.loadFriendList()
                        }
                    }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
                FriendStatus.ACCEPTED -> {
                    IconButton(onClick = {
                        friendsViewModel.handleFriendRequest(friend.frId, false){
                            friendsViewModel.loadFriendList()
                        }
                    }) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
                else -> {
                    Text(text = "未知错误")
                }
            }
        }
    }
}