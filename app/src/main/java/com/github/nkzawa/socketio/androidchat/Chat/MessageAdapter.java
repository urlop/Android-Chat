package com.github.nkzawa.socketio.androidchat.Chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.nkzawa.socketio.androidchat.Chat.DetailMedia.MessageWithImageActivity;
import com.github.nkzawa.socketio.androidchat.Constants;
import com.github.nkzawa.socketio.androidchat.Models.Attachment;
import com.github.nkzawa.socketio.androidchat.Models.Message;
import com.github.nkzawa.socketio.androidchat.R;
import com.github.nkzawa.socketio.androidchat.UtilsMethods;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private int[] mUsernameColors;
    private ChatFragment context;

    public MessageAdapter(ChatFragment context, List<Message> messages) {
        mMessages = messages;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
        case Message.TYPE_MESSAGE:
            layout = R.layout.item_message;
            break;
        case Message.TYPE_ACTION:
            layout = R.layout.item_action;
            break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        Attachment attachment = message.getAttachment();
        viewHolder.setMessage(message.getMessage());
        viewHolder.setUsername(message.getUsername());


        if(attachment != null){
            if(attachment.getFileType() != null){
                if(attachment.getFileType().equals(Constants.MEDIA_IMAGE)){
                    viewHolder.iv_message_image.setVisibility(View.VISIBLE);
                    if(message.getMessageStatus() == Message.MESSAGE_NOT_SENT){
                        sendMessage(message, viewHolder, position);
                    }else{
                            viewHolder.setMessageImage(message);
                    }

                }else if(attachment.getFileType().equals(Constants.MEDIA_VIDEO)){
                    viewHolder.vv_video.setVisibility(View.VISIBLE);
                    if(message.getMessageStatus() == Message.MESSAGE_NOT_SENT){
                        sendMessage(message, viewHolder , position);
                    }else{
                        viewHolder.setVideoView(message);
                    }
                }else{
                    viewHolder.iv_message_image.setVisibility(View.GONE);
                    viewHolder.rl_video_container.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getType();
    }


    public void sendMessage(final Message message, final ViewHolder viewHolder, final int position){

        UtilsMethods.showProgress(true, context.getActivity(), viewHolder.v_progress, null);

        String receiverUserId = null;
        String receiverRoomId = null;

        if(context.getChatActivity().getTypeChat().equals(Constants.USER_CHAT)){
            receiverUserId = ""+message.getReceiverId();
        }else{
            receiverRoomId = ""+message.getReceiverId();
        }

        TypedFile typedFile = null;
        final String typeFile = message.getAttachment().getFileType();

        if(typeFile.equals(Constants.MEDIA_IMAGE)){
            typedFile = new TypedFile("image/jpg", new File(message.getAttachment().getLocalFileUrl()));
        }else{
            typedFile = new TypedFile("video/mp4", new File(message.getAttachment().getLocalFileUrl()));
        }

        context.getRestClient().getWebservices().sendMessage(""+context.getmPreferences().getUserId(),receiverUserId,receiverRoomId,typedFile,message.getMessage(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                UtilsMethods.showProgress(false, context.getActivity(), viewHolder.v_progress, null);
                if(typeFile.equals(Constants.MEDIA_IMAGE)){
                    viewHolder.setMessageImage(message);
                }else{
                    viewHolder.setVideoView(message);
                }
                message.setMessageStatus(Message.MESSAGE_SENT);
                message.save();
                notifyItemChanged(position);
            }

            @Override
            public void failure(RetrofitError error) {
                UtilsMethods.showProgress(false, context.getActivity(), viewHolder.v_progress, null);
            }
        });
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mMessageView;
        private ImageView iv_message_image;
        private VideoView vv_video;
        private RelativeLayout rl_video_container;
        private ProgressBar v_progress;

        public ViewHolder(View itemView) {
            super(itemView);

            mUsernameView = (TextView) itemView.findViewById(R.id.username);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            vv_video = (VideoView) itemView.findViewById(R.id.vv_video);
            iv_message_image = (ImageView) itemView.findViewById(R.id.iv_message_image);
            rl_video_container = (RelativeLayout) itemView.findViewById(R.id.rl_video_container);
            v_progress = (ProgressBar) itemView.findViewById(R.id.v_progress);
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

        private void setMessageImage(Message message) {
            UtilsMethods.showProgress(true, context.getActivity(), v_progress, null);
            String image_url = null;

            if(message.getAttachment().getLocalFileUrl() != null){
                image_url = message.getAttachment().getLocalFileUrl();
            }else{
                image_url = message.getAttachment().getFileUrl();
            }

            if(image_url != null){
                Glide.with(context)
                        .load(image_url)
                        .placeholder(R.drawable.shadow_picture)
                        .error(R.drawable.shadow_picture)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                iv_message_image.setImageResource(R.drawable.shadow_picture);
                                UtilsMethods.showProgress(false, context.getActivity(), v_progress, null);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                iv_message_image.setImageDrawable(resource.getCurrent());
                                UtilsMethods.showProgress(false, context.getActivity(), v_progress, null);
                                return false;
                            }
                        }).into(iv_message_image);
            }

        }

        private void setVideoView(Message message){

            String file_url = null;

            if(message.getAttachment().getLocalFileUrl() != null){
                file_url = message.getAttachment().getLocalFileUrl();
            }else{
                file_url = message.getAttachment().getFileUrl();
            }

            if(file_url != null){
                UtilsMethods.showProgress(true, context.getActivity(), v_progress, null);

                try {
                    MediaController mediaController = new MediaController(context.getActivity());
                    mediaController.setAnchorView(vv_video);
                    Uri video = Uri.parse(file_url);
                    vv_video.setMediaController(mediaController);
                    vv_video.setVideoURI(video);

                    vv_video.start();

                    vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            UtilsMethods.showProgress(false, context.getActivity(), v_progress, null);
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(context.getActivity(), context.getActivity().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            }

        }

        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }
    }



}
