package com.rerere.iwara4a.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.rerere.iwara4a.data.model.comment.Comment
import com.rerere.iwara4a.data.model.comment.CommentPosterType
import com.rerere.iwara4a.data.model.comment.getAllReplies
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.setClipboard

@Composable
fun CommentItem(
    navController: NavController,
    comment: Comment,
    onReply: (Comment) -> Unit
) {
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            onReply(comment)
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(model = comment.authorPic)
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .noRippleClickable {
                            navController.navigate("user/${comment.authorId}")
                        },
                    painter = painter,
                    contentDescription = null
                )

                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .noRippleClickable {
                                    navController.navigate("user/${comment.authorId}")
                                },
                            text = comment.authorName,
                            fontSize = 17.sp
                        )
                        when (comment.posterType) {
                            CommentPosterType.OWNER -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PINK)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "UP主", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            CommentPosterType.SELF -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Yellow)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "你", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            else -> {}
                        }
                        if(comment.fromIwara4a){
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(text = "Iwara4a", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                            }
                        }
                    }
                    Text(text = comment.date, fontSize = 12.sp)
                }
            }
            Text(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            onReply.invoke(comment)
                        },
                        onLongClick = {
                            context.setClipboard(comment.content)
                        }
                    )
                    .padding(horizontal = 4.dp),
                text = comment.content
            )

            // 回复的回复
            val allReplies = comment.getAllReplies()
            var expandReplies by remember { mutableStateOf(allReplies.size <= 1) }
            if(allReplies.isNotEmpty()) {
                Crossfade (expandReplies) {
                    if(!it) {
                        TextButton(onClick = { expandReplies = true }) {
                            Text(
                                text = "共有${allReplies.size}条回复"
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            comment.reply.fastForEach { reply ->
                                ReplyItem(reply, onReply)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReplyItem(comment: Comment, onReply: (Comment) -> Unit) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            onReply(comment)
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(model = comment.authorPic)
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .noRippleClickable {
                            navController.navigate("user/${comment.authorId}")
                        },
                    painter = painter,
                    contentDescription = null
                )

                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .noRippleClickable {
                                    navController.navigate("user/${comment.authorId}")
                                },
                            text = comment.authorName,
                            fontSize = 17.sp
                        )
                        when (comment.posterType) {
                            CommentPosterType.OWNER -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PINK)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "UP主", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            CommentPosterType.SELF -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Yellow)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "你", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            else -> {}
                        }
                        if(comment.fromIwara4a){
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(text = "Iwara4a", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                            }
                        }
                    }
                    Text(text = comment.date, fontSize = 12.sp)
                }
            }
            Text(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            onReply.invoke(comment)
                        },
                        onLongClick = {
                            context.setClipboard(comment.content)
                        }
                    )
                    .padding(horizontal = 4.dp),
                text = comment.content
            )

            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                comment.reply.fastForEach { reply ->
                    ReplyItem(reply, onReply)
                }
            }
        }
    }
}