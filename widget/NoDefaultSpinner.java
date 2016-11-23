package com.ideationdesignservices.txtbook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class NoDefaultSpinner extends Spinner {

    protected class SpinnerAdapterProxy implements InvocationHandler {
        protected Method getView;
        protected SpinnerAdapter obj;

        protected SpinnerAdapterProxy(SpinnerAdapter obj) {
            this.obj = obj;
            try {
                this.getView = SpinnerAdapter.class.getMethod("getView", new Class[]{Integer.TYPE, View.class, ViewGroup.class});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            try {
                return m.equals(this.getView) ? getView(((Integer) args[0]).intValue(), (View) args[1], (ViewGroup) args[2]) : m.invoke(this.obj, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }

        protected View getView(int position, View convertView, ViewGroup parent) throws IllegalAccessException {
            TextView v = (TextView) ((LayoutInflater) NoDefaultSpinner.this.getContext().getSystemService("layout_inflater")).inflate(17367048, parent, false);
            if (position <= 0) {
                v.setTypeface(null, 0);
                v.setText(NoDefaultSpinner.this.getContentDescription());
                return v;
            }
            TextView v2 = (TextView) this.obj.getView(position, convertView, parent);
            v2.setTypeface(null, 1);
            return v2;
        }
    }

    public NoDefaultSpinner(Context context) {
        super(context);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(SpinnerAdapter orig) {
        super.setAdapter(newProxy(orig));
        try {
            Method m = AdapterView.class.getDeclaredMethod("setNextSelectedPositionInt", new Class[]{Integer.TYPE});
            m.setAccessible(true);
            m.invoke(this, new Object[]{Integer.valueOf(-1)});
            Method n = AdapterView.class.getDeclaredMethod("setSelectedPositionInt", new Class[]{Integer.TYPE});
            n.setAccessible(true);
            n.invoke(this, new Object[]{Integer.valueOf(-1)});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected SpinnerAdapter newProxy(SpinnerAdapter obj) {
        return (SpinnerAdapter) Proxy.newProxyInstance(obj.getClass().getClassLoader(), new Class[]{SpinnerAdapter.class}, new SpinnerAdapterProxy(obj));
    }
}
