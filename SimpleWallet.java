package club.karbo.lightwallet;

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

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;


public class SimpleWallet {

  private final String host_default = "127.0.0.1";
  private final int port_default = 15000;
  private final String rpc_v = "2.0";
  private final String id_conn = "EWF8aIFX0y9w";
  private final String log_tag = "LightWallet";
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
    HttpResponse response;
    HttpClient client = new DefaultHttpClient();
    HttpPost conn = new HttpPost("http://" + this.host + ":" + this.port + "/json_rpc");
    try {
      StringEntity params = new StringEntity(req);
      conn.setEntity(params);
      response = client.execute(conn);
      response.addHeader("Content-Type", "application/json; charset=UTF-8");
      buff = EntityUtils.toString(response.getEntity(), "UTF-8");
      this.service_status = true;
	  } catch (ClientProtocolException e){
	  e.printStackTrace();
	  } catch (IOException e){
	  e.printStackTrace();
    }
    return buff;
  }

  public Double[] getBalance(){
	Double[] result = new Double[2];
    result[0] = 0.0;
    result[1] = 0.0;
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
        if (root.getString("id").equals(this.id_conn)){
          JSONObject res = root.getJSONObject("result");
          result[0] = res.getDouble("available_balance");
          result[1] = res.getDouble("locked_amount");
          this.oper_status = true;
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
        if (root.getString("id").equals(this.id_conn)){
          JSONObject res = root.getJSONObject("result");
          result = res.getInt("height");
          this.oper_status = true;
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

}
