package com.oruphones.nativediagnostic;


public interface IMountService extends android.os.IInterface
{
    /** Default implementation for IMountService. */
    public static class Default implements IMountService
    {
        @Override public boolean isUsbMassStorageEnabled() throws android.os.RemoteException
        {
            return false;
        }
        @Override public void setUsbMassStorageEnabled(boolean enable) throws android.os.RemoteException
        {
        }
        @Override public java.lang.String getVolumeState(java.lang.String mountPoint) throws android.os.RemoteException
        {
            return null;
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements IMountService
    {
        private static final java.lang.String DESCRIPTOR = "IMountService";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an IMountService interface,
         * generating a proxy if needed.
         */
        public static IMountService asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof IMountService))) {
                return ((IMountService)iin);
            }
            return new IMountService.Stub.Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_isUsbMassStorageEnabled:
                {
                    data.enforceInterface(descriptor);
                    boolean _result = this.isUsbMassStorageEnabled();
                    reply.writeNoException();
                    reply.writeInt(((_result)?(1):(0)));
                    return true;
                }
                case TRANSACTION_setUsbMassStorageEnabled:
                {
                    data.enforceInterface(descriptor);
                    boolean _arg0;
                    _arg0 = (0!=data.readInt());
                    this.setUsbMassStorageEnabled(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getVolumeState:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _result = this.getVolumeState(_arg0);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements IMountService
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            @Override public boolean isUsbMassStorageEnabled() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_isUsbMassStorageEnabled, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().isUsbMassStorageEnabled();
                    }
                    _reply.readException();
                    _result = (0!=_reply.readInt());
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
            @Override public void setUsbMassStorageEnabled(boolean enable) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(((enable)?(1):(0)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_setUsbMassStorageEnabled, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().setUsbMassStorageEnabled(enable);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public java.lang.String getVolumeState(java.lang.String mountPoint) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(mountPoint);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getVolumeState, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getVolumeState(mountPoint);
                    }
                    _reply.readException();
                    _result = _reply.readString();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
            public static IMountService sDefaultImpl;
        }
        static final int TRANSACTION_isUsbMassStorageEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_setUsbMassStorageEnabled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getVolumeState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        public static boolean setDefaultImpl(IMountService impl) {
            if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }
        public static IMountService getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }
    public boolean isUsbMassStorageEnabled() throws android.os.RemoteException;
    public void setUsbMassStorageEnabled(boolean enable) throws android.os.RemoteException;
    public java.lang.String getVolumeState(java.lang.String mountPoint) throws android.os.RemoteException;
}
