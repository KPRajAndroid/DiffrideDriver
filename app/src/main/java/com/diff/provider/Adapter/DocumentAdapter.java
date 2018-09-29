package com.diff.provider.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.diff.provider.Activity.ShowInvoicePicture;
import com.diff.provider.Helper.URLHelper;
import com.diff.provider.Models.AccessDetails;
import com.diff.provider.Models.Document;
import com.diff.provider.R;
import com.diff.provider.Utilities.Utilities;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> implements AdapterImageUpdateListener {

        private ArrayList<Document> listModels;
        private Context context;
        JSONArray jsonArraylist;
        private RadioButton lastChecked = null;
        BottomSheetBehavior behavior;
        String TAG = "ServiceListAdapter";
        private int pos;
        private ServiceClickListener serviceClickListener;
        Document serviceListModel;
        boolean[] selectedService;
        boolean select;
        ViewHolder viewHolder;
        ViewHolder selectedHolder;
        View clickedView;

        boolean isFront = true;
        boolean isBack = false;


        public DocumentAdapter(ArrayList<Document> listModel, Context context) {
            this.listModels = listModel;
            this.context = context;
        }

        public void setSelect(boolean select) {
            this.select = select;
        }

        @Override
        public void onImageSelectedUpdate(Bitmap bitmap, int pos) {
            Log.e("update_img_listener", bitmap.toString() + "  Pos: " + pos);
            //notifyDataSetChanged();
            viewHolder.updateImageView(pos, bitmap);
        }

        public void setList(ArrayList<Document> list) {
            this.listModels = list;
        }

        public interface ServiceClickListener {
            void onDocImgClick(Document document, int pos);
        }

        public List<Document> getServiceListModel() {
            return listModels;
        }

        public void setServiceClickListener(ServiceClickListener serviceClickListener) {
            this.serviceClickListener = serviceClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reg_doc_list_item, parent, false);
            viewHolder = new ViewHolder(v);
            return new ViewHolder(v);
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            ImageView docImgFront, docImgBack;
            TextView docName;
            DatePickerDialog datePickerDialog;
            ViewHolder viewsHolder;
            Utilities utils = new Utilities();
            String scheduledDate = "";

            ViewHolder(View itemView) {
                super(itemView);
                docImgFront = (ImageView) itemView.findViewById(R.id.doc_image_front);
                docImgBack = (ImageView) itemView.findViewById(R.id.doc_image_back);
                docName = (TextView) itemView.findViewById(R.id.doc_name);

                docImgFront.setOnClickListener(this);
                docImgBack.setOnClickListener(this);
            }

            public void updateImageView(int position, Bitmap bitmap)
            {

                if (isFront)
                    docImgFront.setImageBitmap(bitmap);
                else if (isBack)
                    docImgBack.setImageBitmap(bitmap);
                else
                    docImgFront.setImageBitmap(bitmap);

                notifyItemChanged(position);
            }

            private void showDatePicker(final Document document)
            {
                final Document doc = document;

                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                // set day of month , month and year value in the edit text
                                String choosedMonth = "";
                                String choosedDate = "";
                                String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                scheduledDate = choosedDateFormat;

                                if (dayOfMonth < 10) {
                                    choosedDate = "0" + dayOfMonth;
                                } else {
                                    choosedDate = "" + dayOfMonth;
                                }
                                //afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                //expDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                //if (doc != null)
                                doc.setExpdate(choosedDate + " " + choosedMonth + " " + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                //datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                datePickerDialog.show();
            }

            @Override
            public void onClick(View v) {

                int id = v.getId();
                Document document = (Document) v.getTag();

                if (id == R.id.doc_image_front)
                {
                    isFront = true;
                    isBack = false;

                    pos = getPosition();
                    clickedView = v;
                    if (document.getImg() != null && !document.getImg().equalsIgnoreCase("null") && document.getImg().length() > 0) {
                        showDialog(document);
                    } else {
                        serviceClickListener.onDocImgClick(document, pos);
                    }
                }
                else if (id == R.id.doc_image_back)
                {
                    isFront = false;
                    isBack = true;

                    pos = getPosition();
                    clickedView = v;
                    if (document.getImg() != null && !document.getImg().equalsIgnoreCase("null") && document.getImg().length() > 0) {
                        showDialog(document);
                    } else {
                        serviceClickListener.onDocImgClick(document, pos);
                    }
                }

            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Document serviceListModel = listModels.get(position);

            String name = serviceListModel.getName();
            if (name != null && !name.equalsIgnoreCase("null") && name.length() > 0) {
                holder.docName.setText(name);
            }


            Log.e(TAG, "onBindViewHolder: " + serviceListModel.getImg());
            Log.e(TAG, "onBindViewHolder: bitmap " + serviceListModel.getBitmap());


            if (serviceListModel.getImg() != null && !serviceListModel.getImg().equalsIgnoreCase("null") && serviceListModel.getImg().length() > 0) {
                Log.e(TAG, "onBindViewHolder: " + AccessDetails.serviceurl + "/storage/" + serviceListModel.getImg());

                if (isFront)
                    Picasso.with(context).load(AccessDetails.serviceurl + "/storage/" + serviceListModel.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.doc_placeholder).error(R.drawable.doc_placeholder).into(holder.docImgFront);
                else if (isBack)
                    Picasso.with(context).load(AccessDetails.serviceurl + "/storage/" + serviceListModel.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.doc_placeholder).error(R.drawable.doc_placeholder).into(holder.docImgBack);
                else
                    Picasso.with(context).load(AccessDetails.serviceurl + "/storage/" + serviceListModel.getImg()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.doc_placeholder).error(R.drawable.doc_placeholder).into(holder.docImgFront);
            }

            if ( serviceListModel.getBitmap() != null)
            {
                if (isFront)
                    holder.docImgFront.setImageBitmap(serviceListModel.getBitmap());
                else if (isBack)
                    holder.docImgBack.setImageBitmap(serviceListModel.getBitmap());
                else
                    holder.docImgFront.setImageBitmap(serviceListModel.getBitmap());
            }

            holder.docImgFront.setTag(serviceListModel);
            holder.docImgBack.setTag(serviceListModel);

        }

        @Override
        public int getItemCount() {
            return listModels.size();
        }

        private void showDialog(final Document document) {
            android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.img_click_dialog, null);
            dialogBuilder.setView(dialogView);
            final TextView viewTxt = (TextView) dialogView.findViewById(R.id.view_txt);
            final TextView updateTxt = (TextView) dialogView.findViewById(R.id.update_txt);
            final android.app.AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            viewTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    Intent intent=new Intent(context, ShowInvoicePicture.class);
                    intent.putExtra("image", AccessDetails.serviceurl + "/storage/" + document.getImg());
                    context.startActivity(intent);
                }
            });

            updateTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    serviceClickListener.onDocImgClick(document, pos);
                    alertDialog.dismiss();
                }
            });
        }
    }