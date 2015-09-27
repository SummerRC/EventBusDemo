
package com.angeldevil.eventbusdemo;

import com.angeldevil.eventbusdemo.Event.ItemListEvent;
import com.angeldevil.eventbusdemo.dummy.DummyContent;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ItemListFragment extends ListFragment {

    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Register */
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /** Unregister */
        EventBus.getDefault().unregister(this);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /** ListView设为单选模式 */
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        /** 开启工作线程加载列表 */
        new WorkerThread().start();
    }

    /**
     * 在主线程接收ItemListEvent事件，必须是public void
     * @param event ItemListEvent事件
     */
    public void onEventMainThread(ItemListEvent event) {
        Toast.makeText(getActivity(), event.getItems().toString(), Toast.LENGTH_SHORT).show();
        Log.d(MainActivity.TAG,
                "Received ItemListEvent, is main thread:" + (Looper.myLooper() == Looper.getMainLooper()));
        /** 更新界面 */
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_activated_1, android.R.id.text1, event.getItems()));
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        /** 发送列表项点击事件，直接使用getItem，这里是DummyItem类型 */
        Log.d(MainActivity.TAG, "Clicked item:" + position);
        EventBus.getDefault().post(getListView().getItemAtPosition(position));
    }

    /**
     * 加载列表的工作线程
     */
    private static class WorkerThread extends Thread {
        
        @Override
        public void run() {
            try {
                while (true) {
                    Log.d(MainActivity.TAG, "Start get data at WorkerThread");
                    /** 模拟延时:每3秒钟发送一个事件 */
                    Thread.sleep(3000);
                    /** 发事件，在后台线程发的事件 */
                    Log.d(MainActivity.TAG, "Got data, post ItemListEvent");
                    EventBus.getDefault().post(new ItemListEvent(DummyContent.ITEMS));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
