package com.npat.pm;

import android.app.Activity;
import android.content.Context;

public class PublicUpdate {
	
	public  static void Update(boolean notifi,boolean toast, boolean proccessbar, Activity activity,String UserCode, String PersonCode)
	{
		GetWsAcceptWorkList LWsAcceptWorkList = new GetWsAcceptWorkList(activity, PersonCode,toast,proccessbar,notifi);
		LWsAcceptWorkList.AsyncExecute();
		//***************************************
		GetWsMyWorkList LWsMyWorks = new GetWsMyWorkList(activity, PersonCode,toast,proccessbar,notifi);
		LWsMyWorks.AsyncExecute();
		//***************************************
		GetWsGetLocation LWsGetLocation = new GetWsGetLocation(activity,toast,proccessbar);
		LWsGetLocation.AsyncExecute();
		//***************************************
		GetWsGetRade LWsGetRade = new GetWsGetRade(activity,toast,proccessbar);
		LWsGetRade.AsyncExecute();
		//***************************************
		GetWsGetMyWorkRequest LWsGetMyWorkRequest = new GetWsGetMyWorkRequest(activity,UserCode,toast,proccessbar,notifi);
		LWsGetMyWorkRequest.AsyncExecute();
		//***************************************
		GetWsOtherWorkStatusReport LWsOtherWorkStatusReport = new GetWsOtherWorkStatusReport(activity,PersonCode,toast,proccessbar,notifi);
		LWsOtherWorkStatusReport.AsyncExecute();
		//***************************************
		GetListHamkaran LWSyncGetListHamkaran = new GetListHamkaran(activity,PersonCode,toast,proccessbar);
		LWSyncGetListHamkaran.AsyncExecute();
		//***************************************
		GetCheckUserStatus LWSyncCheckUserStatus = new GetCheckUserStatus(activity,UserCode,toast,proccessbar);
		LWSyncCheckUserStatus.AsyncExecute();
	}
	public  static void Update(boolean notifi, boolean toast, boolean proccessbar, Context activity, String UserCode, String PersonCode)
	{
		SyncWsAcceptWorkList LWsAcceptWorkList = new SyncWsAcceptWorkList(activity, PersonCode,toast,proccessbar,notifi);
		LWsAcceptWorkList.AsyncExecute();
		//***************************************
		SyncWsMyWorkList LWsMyWorks = new SyncWsMyWorkList(activity, PersonCode,toast,proccessbar,notifi);
		LWsMyWorks.AsyncExecute();
		//***************************************
		SyncWsGetLocation LWsGetLocation = new SyncWsGetLocation(activity,toast,proccessbar);
		LWsGetLocation.AsyncExecute();
		//***************************************
		SyncWsGetRade LWsGetRade = new SyncWsGetRade(activity,toast,proccessbar);
		LWsGetRade.AsyncExecute();
		//***************************************
		SyncWsGetMyWorkRequest LWsGetMyWorkRequest = new SyncWsGetMyWorkRequest(activity,UserCode,toast,proccessbar,notifi);
		LWsGetMyWorkRequest.AsyncExecute();
		//***************************************
		SyncWsOtherWorkStatusReport LWsOtherWorkStatusReport = new SyncWsOtherWorkStatusReport(activity,PersonCode,toast,proccessbar,notifi);
		LWsOtherWorkStatusReport.AsyncExecute();
		//***************************************
		SyncGetListHamkaran LWSyncGetListHamkaran = new SyncGetListHamkaran(activity,PersonCode,toast,proccessbar);
		LWSyncGetListHamkaran.AsyncExecute();
		//***************************************
		SyncCheckUserStatus LWSyncCheckUserStatus = new SyncCheckUserStatus(activity,UserCode,toast,proccessbar);
		LWSyncCheckUserStatus.AsyncExecute();
	}
}
