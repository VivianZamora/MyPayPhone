package com.example.mypayphone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText txtCelular;
    EditText txtCodPais;
    EditText txtIdentificacion;
    EditText txtReferencia;
    EditText  txtMonto;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }


    public void Donar(View view) {
        //obtener referencias de botones
        txtMonto = (EditText) findViewById(R.id.txtcantidaddonar);
        txtIdentificacion = (EditText)findViewById(R.id.txtidentificacion);
        txtCodPais= (EditText) findViewById(R.id.editTextcodepais);
        txtCelular= (EditText)findViewById(R.id.txtnumtelefonico);
        txtReferencia= (EditText) findViewById(R.id.txtadjunto);
        requestPago();
    }


    private void requestPago() {
        String url = "https://pay.payphonetodoesposible.com/api/Sale";
        HashMap<String, String> hash = new HashMap<>();
        /*"phoneNumber": "986616379",
                "countryCode": "593"
        "clientUserId": "1207028109",
                "reference": "none",
                "amount": 100,
                "amountWithTax": 90,
                "amountWithoutTax": 0,
                "tax": 10,
                "clientTransactionId": "12345"*/
        hash.put("phoneNumber", txtCelular.getText().toString());
        hash.put("countryCode", txtCodPais.getText().toString());
        hash.put("clientUserId", txtIdentificacion.getText().toString());
        hash.put("reference", txtReferencia.getText().toString());
        hash.put("responseUrl", "http://paystoreCZ.com/confirm.php");

        double amount, tax, amountWithTax;
        amount = Double.parseDouble(txtMonto.getText().toString());
        amount = amount * 100;
        int intAmount, intTax, intAamountWithTax;
        intAmount = (int) Math.round(amount);
        hash.put("amount", String.valueOf(intAmount));

        tax = amount * 0.06;
        tax = (intAmount * tax / 100);

        if (tax > 0) {
            intTax = (int) Math.round(tax);
            hash.put("tax", String.valueOf(intTax));
            amountWithTax = amount - tax;
            intAamountWithTax = (int) Math.round(amountWithTax);
            hash.put("amountWithTax", String.valueOf(intAamountWithTax));

            hash.put("amountWithoutTax", "0");
        } else {
            hash.put("amountWithTax", "0");
            hash.put("amountWithoutTax", String.valueOf(intAmount));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(new Date());
        hash.put("clientTransactionId", date);
        Toast.makeText(MainActivity.this, "Se generó la petición: " + date, Toast.LENGTH_LONG).show();

        JSONObject js = new JSONObject(hash);
        //txtJson.setText(js.toString());
        System.out.println(js.toString());
        //JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, url, createMyReqSuccessListener(), createMyReqErrorListener()) { protected Map<String, String> getParams() throws com.android.volley.AuthFailureError { Map<String, String> params = new HashMap<String, String>(); params.put("param1", num1);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String transaction = response.getString("transactionId");
                    Toast.makeText(MainActivity.this, "Se completó la transacción: " + transaction, Toast.LENGTH_LONG).show();
                    //txtJson.append(",\n{\"transactionId\":\"" + transaction + "\"}");

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Toast.makeText(MainActivity.this, "Incorrecto\n" + ex.getMessage(), Toast.LENGTH_LONG).show();

                System.out.println(ex.toString());
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Accept", "application/json");
                headerMap.put("Authorization", "Bearer l-590WJNdSj3ppoUjqYhrIRBJEp6PiR349aO7OvYaXk10LcIMEE2lxfOEM4KnoNuoRdw7pmmzuiXPmP9k6xcJuQvf4u-6ZmmNSJBo5vIMrcydM8ccUvUdvHRivVPtYCJ32ifwuwshXGKFrt_s-ivyu0EBEaQlSsoe8OlQIpwK8JSLQr59fv_c10eOKbxuVs3PDCRiZD022E27KTPMSWdL70DlA2-SkCBMPpF6HWXTgvpDdpdUiQ8EtvYdQoZVNO_uMx6e5V0Vr_NzcbB049CATv4JOYE4idm1RTtceFQmUFwYyK9o9jani1nbyCDNB_VszVYne8YO0dfQzgxWnfn04hYOUo");
                return headerMap;
            }
        };
        requestQueue.add(jsonRequest);
    }
}