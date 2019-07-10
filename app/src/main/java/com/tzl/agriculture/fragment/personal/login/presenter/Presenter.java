package com.tzl.agriculture.fragment.personal.login.presenter;


import com.tzl.agriculture.fragment.personal.login.model.ILoginSignIn;
import com.tzl.agriculture.fragment.personal.login.model.ModelSignIn;
import com.tzl.agriculture.fragment.personal.login.view.IView;
import com.tzl.agriculture.view.BasePresenter;

/**
 *
 */

public class Presenter extends BasePresenter<IView> {

    ILoginSignIn iLoginSignIn=new ModelSignIn();

    public void setSignIn(String name,String openId)
    {
       iLoginSignIn.onSignIn(name, openId, new ILoginSignIn.IOnSetListenter() {
           IView view=getView();
           @Override
           public void onError(String error) {
               if(view!=null){
                    view.showToast(error);
               }
           }
           @Override
           public void onSccess(String repsonce) {
               if(view!=null){
                   view.showToast(repsonce);
               }
           }
       });
    }
}
