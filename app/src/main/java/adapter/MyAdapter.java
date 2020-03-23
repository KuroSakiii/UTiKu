package adapter;

import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daquexian.flexiblerichtextview.FlexibleRichTextView;
import com.example.administrator.aninterface.R;

import java.util.List;

import listclass.Que;

public class MyAdapter extends BaseAdapter {
    private List<Que> mList;//数据源
    private LayoutInflater mInflater;//布局装载器对象

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public MyAdapter(Context context, List<Que> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return mList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int position) {
        return position;
    }

    @Override
    //返回每一项的显示内容
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 由于我们只需要将XML转化为View，并不涉及到具体的布局，所以第二个参数通常设置为null
            convertView = mInflater.inflate(R.layout.item, null);

            //对viewHolder的属性进行赋值
            viewHolder.imageView = convertView.findViewById(R.id.iv_image);
            viewHolder.title = convertView.findViewById(R.id.tv_title);
            viewHolder.que = convertView.findViewById(R.id.tv_content);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {
            //如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出que对象
        Que que = mList.get(position);

        // 设置控件的数据
        viewHolder.imageView.setImageResource(que.ImageRes);
        viewHolder.title.setText(que.Content);
        viewHolder.que.setText(que.Que);

        return convertView;
    }

    // ViewHolder用于缓存控件，三个属性分别对应item布局文件的三个控件
    class ViewHolder {
        ImageView imageView;
        TextView title;
        FlexibleRichTextView que;
    }
}
