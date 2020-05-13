package com.pixel.mycontact.daos;

import com.pixel.mycontact.beans.IMMessage;
import com.pixel.mycontact.beans.People;
import com.pixel.mycontact.utils.LogUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Carl Su
 * @date 2020/5/10
 */
public class RealmTransactions {
    private static final String TAG = "RealmTransactions";
    private Realm mRealm;

    public RealmTransactions(Realm realm) {
        this.mRealm = realm;
    }

    public void insertContacts(final List<People> list, Callback callback) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(list);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "insert multi ok");
                callback.onSuccess();
            }
        }, error -> {
            LogUtil.e(TAG, "insert bad:" + error.getMessage());
            callback.onFailed(error.getMessage());
        });
    }


    public void insertAContact(final People people, Callback callback) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealm(people),
                () -> {
                    LogUtil.d(TAG, "insert ok");
                    callback.onSuccess();
                },
                error -> {
                    LogUtil.e(TAG, "insert bad: " + error.getMessage());
                    callback.onFailed(error.getMessage());
                });
    }

    public void updateContact(People people, Callback callback) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(people),
                () -> {
                    LogUtil.d(TAG, "update ok");
                    callback.onSuccess();
                },
                error -> {
                    LogUtil.e(TAG, "update bad" + error.getMessage());
                    callback.onFailed(error.getMessage());
                });
    }

    public List<People> queryAll() {
        RealmResults<People> realmResults = mRealm.where(People.class).findAll();
        return mRealm.copyFromRealm(realmResults);
    }

    public void deleteContact(String uuid, Callback callback) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults results = realm.where(People.class).equalTo("uuid", uuid).findAll();
                results.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                callback.onFailed(error.getMessage());
                LogUtil.e(TAG,error.getMessage());
            }
        });
    }

    public void saveMessages(List<IMMessage> imMessages) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(imMessages),
                () -> LogUtil.d(TAG, "save msg ok"),
                error -> LogUtil.e(TAG, "Save msg bad"));
    }

    public List<IMMessage> loadMessages(){
        RealmResults<IMMessage> realmResults = mRealm.where(IMMessage.class).findAll();
        return mRealm.copyFromRealm(realmResults);
    }

    public interface Callback {
        void onSuccess();

        void onFailed(String reason);
    }
}
