package com.rerere.iwara4a.ui.screen.video.tabs

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.material.placeholder
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.SmartLinkText
import com.rerere.iwara4a.ui.screen.video.VideoViewModel
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.*
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun VideoScreenDetailTab(
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item {
            // 视频简介
            VideoDetail(videoDetail, videoViewModel)
        }

        item {
            // 作者更多视频
            AuthorMoreVideo(videoDetail)
        }
    }
}

@Composable
private fun VideoDetail(videoDetail: VideoDetail, videoViewModel: VideoViewModel) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    var expand by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 标题
                Text(
                    modifier = Modifier.weight(1f),
                    text = videoDetail.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
                // 更多
                IconButton(onClick = { expand = !expand }) {
                    Icon(if (!expand) Icons.Rounded.ExpandMore else Icons.Rounded.ExpandLess, null)
                }
            }

            // 视频信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "在 ${videoDetail.postDate} 上传",
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    modifier = Modifier.size(17.dp),
                    painter = painterResource(R.drawable.play_icon),
                    contentDescription = null
                )
                Text(text = videoDetail.watchs)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    modifier = Modifier.size(17.dp),
                    painter = painterResource(R.drawable.like_icon),
                    contentDescription = null
                )
                Text(text = videoDetail.likes)
            }

            // 介绍
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                SelectionContainer {
                    SmartLinkText(
                        text = videoDetail.description,
                        maxLines = if (expand) Int.MAX_VALUE else 5,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // 操作
            Actions(videoDetail, videoViewModel, expand)
        }
    }
}

@Composable
private fun ColumnScope.Actions(
    videoDetail: VideoDetail,
    videoViewModel: VideoViewModel,
    expand: Boolean
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    // 操作
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 作者头像
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .noRippleClickable {
                    navController.navigate("user/${videoDetail.authorId}")
                }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = rememberImagePainter(videoDetail.authorPic),
                contentDescription = null
            )
        }

        // 作者名字
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                modifier = Modifier
                    .noRippleClickable {
                        navController.navigate("user/${videoDetail.authorId}")
                    },
                text = videoDetail.authorName
            )
        }

        // 关注
        Button(
            onClick = {
                videoViewModel.handleFollow { action, success ->
                    if (action) {
                        Toast
                            .makeText(
                                context,
                                if (success) "${context.stringResource(id = R.string.follow_success)} ヾ(≧▽≦*)o" else context.stringResource(
                                    id = R.string.follow_fail
                                ),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    } else {
                        Toast
                            .makeText(
                                context,
                                if (success) context.stringResource(id = R.string.unfollow_success) else context.stringResource(
                                    id = R.string.unfollow_fail
                                ),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            }
        ) {
            Text(
                text = if (videoDetail.follow) stringResource(id = R.string.follow_status_following) else "+ ${
                    stringResource(
                        id = R.string.follow_status_not_following
                    )
                }"
            )
        }
        // 喜欢视频
        Button(
            onClick = {
                videoViewModel.handleLike { action, success ->
                    if (action) {
                        Toast
                            .makeText(
                                context,
                                if (success) "${context.stringResource(id = R.string.screen_video_description_liking_success)} ヾ(≧▽≦*)o" else context.stringResource(
                                    id = R.string.screen_video_description_liking_fail
                                ),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    } else {
                        Toast
                            .makeText(
                                context,
                                if (success) context.stringResource(id = R.string.screen_video_description_unlike_success) else context.stringResource(
                                    id = R.string.screen_video_description_unlike_fail
                                ),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                }
            }
        ) {
            Icon(Icons.Rounded.Favorite, null)
            Text(
                text = if (videoDetail.isLike) {
                    stringResource(id = R.string.screen_video_description_like_status_liked)
                } else {
                    stringResource(
                        id = R.string.screen_video_description_like_status_no_like
                    )
                }
            )
        }
    }
    // 展开操作
    AnimatedVisibility(expand) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { navController.navigate("playlist?nid=${videoDetail.nid}") }
            ) {
                Text(text = stringResource(id = R.string.screen_video_description_playlist))
            }
            OutlinedButton(
                onClick = { context.shareMedia(MediaType.VIDEO, videoDetail.id) }
            ) {
                Text(text = stringResource(id = R.string.screen_video_description_share))
            }
            val downloadDialog = rememberMaterialDialogState()
            val exist by produceState(initialValue = false) {
                value = AppContext.database.getDownloadedVideoDao()
                    .getVideo(videoDetail.nid) != null
            }
            MaterialDialog(
                dialogState = downloadDialog,
                buttons = {
                    button(stringResource(id = R.string.screen_video_description_download_button_inapp)) {
                        if (!exist) {
                            val first = videoDetail.videoLinks.firstOrNull()
                            first?.let {
                                context.downloadVideo(
                                    url = first.toLink(),
                                    videoDetail = videoDetail
                                )
                                Toast
                                    .makeText(
                                        context,
                                        context.stringResource(id = R.string.screen_video_description_download_button_inapp_add_queue),
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                                downloadDialog.hide()
                            } ?: kotlin.run {
                                Toast.makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_video_description_download_fail_resolve),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.stringResource(id = R.string.screen_video_description_download_complete),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    button(context.stringResource(id = R.string.screen_video_description_download_button_copy_link)) {
                        val first = videoDetail.videoLinks.firstOrNull()
                        first?.let {
                            context.setClipboard(first.toLink())
                        } ?: kotlin.run {
                            Toast.makeText(
                                context,
                                context.stringResource(id = R.string.screen_video_description_download_fail_resolve),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        downloadDialog.hide()
                    }
                }
            ) {
                title(stringResource(id = R.string.screen_video_description_download_button_title))
                message(stringResource(id = R.string.screen_video_description_download_button_message))
            }
            OutlinedButton(
                onClick = { downloadDialog.show() }
            ) {
                Text(text = stringResource(id = R.string.screen_video_description_download_button_label))
            }
        }
    }
}

@Composable
private fun AuthorMoreVideo(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    // 更多视频
    Text(
        text = "${stringResource(id = R.string.screen_video_other_uploads)}:",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )

    videoDetail.moreVideo.chunked(2).forEach {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            it.forEach {
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("video/${it.id}")
                        }
                ) {
                    Column {
                        val painter = rememberImagePainter(it.pic)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .placeholder(visible = painter.state is ImagePainter.State.Loading)
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth
                            )
                        }

                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(text = it.title, maxLines = 1)
                            Text(
                                text = "${stringResource(id = R.string.screen_video_views)}: ${it.watchs} ${
                                    stringResource(
                                        id = R.string.screen_video_likes
                                    )
                                }: ${it.likes}",
                                maxLines = 1,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}