package adapter;

import android.content.Context;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.aninterface.R;

import java.util.List;

import listclass.Subject;

public class SubjectAdapter extends BaseAdapter {
    private List<Subject> mList;//数据源
    private LayoutInflater mInflater;//布局装载器对象

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public SubjectAdapter(Context context, List<Subject> list) {
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
            convertView = mInflater.inflate(R.layout.item_subject, null);

            //对viewHolder的属性进行赋值
            viewHolder.imageView_1 = convertView.findViewById(R.id.iv_image);
            viewHolder.subject = convertView.findViewById(R.id.tv_subject_name);
            viewHolder.imageView_2 = convertView.findViewById(R.id.iv_image_to);

            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        } else {
            //如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 取出sub对象
        Subject sub = mList.get(position);

        // 设置控件的数据
        viewHolder.imageView_1.setImageResource(sub.ImageRes_1);
        viewHolder.subject.setText(sub.Subject_Name);
        viewHolder.imageView_2.setImageResource(sub.ImageRes_2);

        return convertView;
    }

    // ViewHolder用于缓存控件，三个属性分别对应item_subject布局文件的三个控件
    class ViewHolder {
        ImageView imageView_1;
        TextView subject;
        ImageView imageView_2;
    }
}
