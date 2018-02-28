package com.clj.blesample.operation;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;
//服务列表activity
//api版本兼容相关
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//TODO:Fragment
public class ServiceListFragment extends Fragment {

    private TextView txt_name, txt_mac;
    private ResultAdapter mResultAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_list, null);
        initView(v);
        showData();
        return v;
    }

    private void initView(View v) {
        txt_name = (TextView) v.findViewById(R.id.txt_name);
        txt_mac = (TextView) v.findViewById(R.id.txt_mac);

        mResultAdapter = new ResultAdapter(getActivity());
        ListView listView_device = (ListView) v.findViewById(R.id.list_service);
        //设置adapter
        listView_device.setAdapter(mResultAdapter);
        //item listener
        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattService service = mResultAdapter.getItem(position);
                ((OperationActivity) getActivity()).setBluetoothGattService(service);
                ((OperationActivity) getActivity()).changePage(1);
            }
        });
    }
    //显示数据
    private void showData() {
        //获取gatt对象
        BleDevice bleDevice = ((OperationActivity) getActivity()).getBleDevice();//获取ble设备
        String name = bleDevice.getName();//获取名字
        String mac = bleDevice.getMac();//获取mac
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);//获取该名字和mac的gatt对象
        //显示名字和mac
        txt_name.setText(String.valueOf(getActivity().getString(R.string.name) + name));
        txt_mac.setText(String.valueOf(getActivity().getString(R.string.mac) + mac));
        //TODO:这个不知道干嘛的
        mResultAdapter.clear();
        for (BluetoothGattService service : gatt.getServices()) {
            mResultAdapter.addResult(service);
        }
        mResultAdapter.notifyDataSetChanged();
    }
    //结果适配器
    private class ResultAdapter extends BaseAdapter {
        //属性
        private Context context;
        private List<BluetoothGattService> bluetoothGattServices;
        //构造方法
        ResultAdapter(Context context) {
            this.context = context;
            bluetoothGattServices = new ArrayList<>();
        }
        //添加结果
        void addResult(BluetoothGattService service) {
            bluetoothGattServices.add(service);
        }
        //清空
        void clear() {
            bluetoothGattServices.clear();
        }
        //获取计数
        @Override
        public int getCount() {
            return bluetoothGattServices.size();
        }
        //获取bluetoothGattServices[]
        @Override
        public BluetoothGattService getItem(int position) {
            if (position > bluetoothGattServices.size())
                return null;
            return bluetoothGattServices.get(position);
        }
        //获取item id(直接返回0)
        @Override
        public long getItemId(int position) {
            return 0;
        }
        //TODO:获取view (学习adapter相关)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_service, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
                holder.txt_uuid = (TextView) convertView.findViewById(R.id.txt_uuid);
                holder.txt_type = (TextView) convertView.findViewById(R.id.txt_type);
            }

            BluetoothGattService service = bluetoothGattServices.get(position);
            String uuid = service.getUuid().toString();

            holder.txt_title.setText(String.valueOf(getActivity().getString(R.string.service) + "(" + position + ")"));
            holder.txt_uuid.setText(uuid);
            holder.txt_type.setText(getActivity().getString(R.string.type));
            return convertView;
        }

        class ViewHolder {
            TextView txt_title;
            TextView txt_uuid;
            TextView txt_type;
        }
    }

}
