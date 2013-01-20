package fr.ds.android.rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ds.android.rest.rest.R;

import java.util.List;

public class ArrayTweetAdapter extends ArrayAdapter<Tweet>{

	private List<Tweet> 	mItems;
	private LayoutInflater inflater;
	private Context 			mContext;
	private ProgressDialog 		mDialog 		= null;

	public ArrayTweetAdapter(Context context, List<Tweet> items) {
		super(context, R.layout.row, items);
		this.mContext 	= context;
        this.mItems = items;
       //inflater		= LayoutInflater.from(context);
	}

    public List<Tweet> getItems() {
        return mItems;
    }

    static class ViewHolder {
        public TextView nameView;
        public TextView infoView;
        public ImageView iconeView;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
		if (rowView == null) {
			inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row, null);
		    //convertView = View.inflate(mContext, R.layout.row, null);
            holder = new ViewHolder();
            holder.nameView = (TextView) rowView.findViewById(R.id.name);
            holder.infoView = (TextView) rowView.findViewById(R.id.info);
            //holder.iconeView = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(holder);
        }else {
            holder = (ViewHolder) rowView.getTag();
        }

        //récupération du tweet identifié par sa position dans çla liste
        Tweet item = mItems.get(position);

        //Valorisation du Texte1
        holder.nameView.setText(item.getUser());
        //Valorisation du Texte2
        holder.infoView.setText(item.getText());

        //Affichage de l’icône correspondante
        //holder.iconeView.setImageResource(item.getIcone());

        return rowView;
	}

}
