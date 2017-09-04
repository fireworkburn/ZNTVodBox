
package com.znt.diange.fragment; 

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.diange.R;
import com.znt.diange.entity.AdverInfor;

/** 
 * @ClassName: HelpItemFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-9 下午4:29:17  
 */
public class HelpItemFragment extends BaseFragment
{
	private ImageView imageView = null;
	private TextView tvTitle = null;
	private TextView tvContent = null;
	
	private AdverInfor infor = null;
	
	public HelpItemFragment()
	{
		
	}
	
	public HelpItemFragment(AdverInfor infor)
	{
		this.infor = infor;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		View view = inflater.inflate(R.layout.view_help_item, container, false);  
		imageView = (ImageView)view.findViewById(R.id.iv_help_item);
		tvTitle = (TextView)view.findViewById(R.id.tv_help_item_title);
		tvContent = (TextView)view.findViewById(R.id.tv_help_item_content);
		// TODO Auto-generated method stub
		return view;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		
		showInfor();
		
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	
	private void showInfor()
	{
		Picasso.with(getActivity()).load(infor.getImageRes()).into(imageView);
		tvTitle.setText(infor.getTitle());
		tvContent.setText(infor.getContent());
	}

	/**
	*callbacks
	*/
	@Override
	protected void lazyLoad()
	{
		// TODO Auto-generated method stub
		
	}
}
 
