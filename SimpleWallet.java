package club.karbo.karbolightwallet;

// Copyright (c) 2017 The Karbowanec developers
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.util.Log;


public class SimpleWallet {

  private final String host_default = "127.0.0.1";
  private final int port_default = 15000;
  private final String rpc_v = "2.0";
  private final String id_conn = "EWF8aIFX0y9w";
  private final String log_tag = "KarboLightWallet";
  private String host = null;
  private int port = 0;
  private Boolean service_status = false;
  private Boolean oper_status = false;
  
  public SimpleWallet(String host, int port){
    if (host != null && port != 0){
      this.host = host;
      this.port = port;
      } else {
      this.host = this.host_default;
      this.port = this.port_default;
    }
  }

  private String doServiceWallet(String req){
    String buff = "";
    this.service_status = false;
    URL url;
    HttpURLConnection conn = null;
    try {
      url = new URL("http://" + this.host + ":" + this.port + "/json_rpc");
      conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000);
      conn.setConnectTimeout(10000);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      conn.setDoInput(true);
      conn.setDoOutput(true);
      OutputStream os = conn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(os, "UTF-8"));
      writer.write(req);
      writer.flush();
      writer.close();
      os.close();
      int responseCode = conn.getResponseCode();
      if (responseCode == HttpsURLConnection.HTTP_OK){
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = br.readLine()) != null){
          buff += line;
        }
        this.service_status = true;
      }
      } catch (Exception e){
        e.printStackTrace();
      } finally {
        if (conn != null){
          conn.disconnect();
        }
    }
    return buff;
  }

  public String getAddress(){
	String result = "";
    String buff = "";
    JSONObject args = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "getaddress");
	  } catch (JSONException e){
      e.printStackTrace();
	}
    buff = this.doServiceWallet(args.toString());
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            JSONObject res = root.getJSONObject("result");
            result = res.getString("address");
            this.oper_status = true;
          }
        }
	    } catch (JSONException e){
		e.printStackTrace();
      }
    }
    if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
      Log.d(this.log_tag, "wallet: fail");
    }
    return result;
  }
  
  public int getHeight(){
	int result = 0;
    String buff = "";
    JSONObject args = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "get_height");
	  } catch (JSONException e){
      e.printStackTrace();
	}
    buff = this.doServiceWallet(args.toString());
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            JSONObject res = root.getJSONObject("result");
            result = res.getInt("height");
            this.oper_status = true;
          }
        }
	    } catch (JSONException e){
		e.printStackTrace();
      }
    }
    if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
      Log.d(this.log_tag, "wallet: fail");
    }
    return result;
  }

  public void doReset(){
    String buff = "";
    JSONObject args = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "reset");
	  } catch (JSONException e){
      e.printStackTrace();
	}
    buff = this.doServiceWallet(args.toString());
    Log.d(this.log_tag, buff);
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            this.oper_status = true;
          }
        }
	    } catch (JSONException e){
		e.printStackTrace();
      }
    }
    if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
      Log.d(this.log_tag, "wallet: fail");
    }
  }

  public long[] getBalance(){
    long[] result = new long[2];
    result[0] = 0;
    result[1] = 0;
    String buff = "";
    JSONObject args = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "getbalance");
	  } catch (JSONException e){
      e.printStackTrace();
	}
    buff = this.doServiceWallet(args.toString());
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            JSONObject res = root.getJSONObject("result");
            result[0] = res.getLong("balance");
            result[1] = res.getLong("unlocked_balance");
            this.oper_status = true;
          }
        }
	    } catch (JSONException e){
		e.printStackTrace();
      }
    }
    if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
      Log.d(this.log_tag, "wallet: fail");
    }
    return result;
  }

  public String doTransfer(JSONArray destinations, String payment_id){
    String result = "";
    String buff = "";
    JSONObject args = new JSONObject();
    JSONObject params = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "transfer");
      params.put("destinations", destinations);
      params.put("fee", 1000000000L);
      params.put("mixin", 0);
      params.put("unlock_time", 0);
      params.put("payment_id", payment_id);
      args.put("params", params);
      } catch (JSONException e){
      e.printStackTrace();
    }
    Log.d(this.log_tag, args.toString());
    buff = this.doServiceWallet(args.toString());
    Log.d(this.log_tag, buff);
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            JSONObject res = root.getJSONObject("result");
              result = res.getString("tx_hash");
              this.oper_status = true;
            }
          }
        } catch (JSONException e){
        e.printStackTrace();
      }
	}
	if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
	  Log.d(this.log_tag, "wallet: fail");
	}
    return result;
  }
  
  public Object[][] getTransfers(){
    Object[][] result = new Object[0][0];
    String buff = "";
    JSONObject args = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "get_transfers");
      } catch (JSONException e){
      e.printStackTrace();
    }
    buff = this.doServiceWallet(args.toString());
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            JSONObject res = root.getJSONObject("result");
            if (res.has("transfers")){
              JSONArray transfers = res.getJSONArray("transfers");
              int size = transfers.length();
              if(size > 0){
                result = new Object[size][9];
                for (int i = 0; i < size; i++){
                  JSONObject items_obj = transfers.getJSONObject(i);
                  result[i][0] = new String("");
                  if (items_obj.has("address")){
                    result[i][0] = new String(items_obj.getString("address"));
                  }
                  result[i][1] = (long) 0;
                  if (items_obj.has("amount")){
                    result[i][1] = (long) items_obj.getLong("amount");
                  }
                  result[i][2] = (int) 0;
                  if (items_obj.has("blockIndex")){
                    result[i][2] =(int) items_obj.getInt("blockIndex");
                  }
                  result[i][3] = (long) 0;
                  if (items_obj.has("fee")){
                    result[i][3] = (long) items_obj.getLong("fee");
                  }
                  result[i][4] = (Boolean) false;
                  if (items_obj.has("output")){
                    result[i][4] = (Boolean) items_obj.getBoolean("output");
                  }
                  result[i][5] = new String("");
                  if (items_obj.has("paymentId")){
                    result[i][5] = new String(items_obj.getString("paymentId"));
                  }
                  result[i][6] = (int) 0;
                  if (items_obj.has("time")){
                    result[i][6] = (int) items_obj.getInt("time");
                  }
                  result[i][7] = new String("");
                  if (items_obj.has("transactionHash")){
                    result[i][7] = new String(items_obj.getString("transactionHash"));
                  }
                  result[i][8] = (int) 0;
                  if (items_obj.has("unlockTime")){
                    result[i][8] = (int) items_obj.getInt("unlockTime");
                  }
                }
              }
            }
            this.oper_status = true;
          }
        }
      } catch (JSONException e){
        e.printStackTrace();
      }
    }
    if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
      Log.d(this.log_tag, "wallet: fail");
    }
    return result;
  }

  public String getSeed(){
    String result = "";
    String buff = "";
    JSONObject args = new JSONObject();
    JSONObject params = new JSONObject();
    this.oper_status = false;
    try {
      args.put("jsonrpc", this.rpc_v);
      args.put("id", this.id_conn);
      args.put("method", "query_key");
      params.put("key_type", "mnemonic");
      args.put("params", params);
      } catch (JSONException e){
      e.printStackTrace();
    }
    buff = this.doServiceWallet(args.toString());
    if (this.service_status){
      try {
        JSONObject root = new JSONObject(buff);
        if (root.isNull("error")){
          if (root.getString("id").equals(this.id_conn)){
            JSONObject res = root.getJSONObject("result");
              result = res.getString("key");
              this.oper_status = true;
            }
          }
        } catch (JSONException e){
        e.printStackTrace();
      }
	}
	if (this.oper_status){
      Log.d(this.log_tag, "wallet: success");
      } else {
	  Log.d(this.log_tag, "wallet: fail");
	}
    return result;
  }
  
  public Boolean getOperStatus(){
    return this.oper_status;
  }

  @SuppressLint("DefaultLocale")
  public static String AmountToString(long amount){
    String res = "";
    DecimalFormat df = new DecimalFormat("#.00##########");
    res = df.format(amount * 0.000000000001);
    return res;
  }

  @SuppressLint("TrulyRandom")
  public static String genPaymentId(){
    String res = "";
    SecureRandom random = new SecureRandom();
    byte bytes[] = new byte[32];
    random.nextBytes(bytes);
    StringBuffer result = new StringBuffer();
    for (byte b : bytes){
      result.append(String.format("%02X", b));
    }
    res = result.toString();
    return res;
  }

}
