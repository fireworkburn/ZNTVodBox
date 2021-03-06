/**
 * siir.es.adbWireless.adbWireless.java This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 **/

package com.znt.speaker.util;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Debug;

class AutoConnectTask extends AsyncTask<Void, Void, Void>
{

	private String url;

	public AutoConnectTask(String u)
	{
		this.url = u;
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		try
		{
			URI url = new URI(this.url);
			System.out.println("url = " + this.url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet method = new HttpGet(url);
			httpClient.execute(method);
		}
		catch (Exception e)
		{
			
		}
		return null;
	}

}