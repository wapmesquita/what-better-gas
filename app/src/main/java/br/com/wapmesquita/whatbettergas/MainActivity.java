package br.com.wapmesquita.whatbettergas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private EditText txtKmGas;
    private EditText txtKmEtanol;
    private EditText txtValueGas;
    private EditText txtValueEtanol;
    private EditText txtDiscount;
    private EditText txtValueDescGas;
    private EditText txtValueDescEtanol;
    private ConfigHelper config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = new ConfigHelper(this);

        txtKmGas = (EditText)findViewById(R.id.txtKmGas);
        txtKmEtanol = (EditText)findViewById(R.id.txtKmEtanol);
        txtValueGas = (EditText)findViewById(R.id.txtValueGas);
        txtValueEtanol = (EditText)findViewById(R.id.txtValueEtanol);
        txtDiscount = (EditText)findViewById(R.id.txtDiscount);
        txtValueDescGas = (EditText)findViewById(R.id.txtValueDescGas);
        txtValueDescEtanol = (EditText)findViewById(R.id.txtValueDescEtanol);

        loadConfig();
        calculateValue();
    }

    private void loadConfig() {
        Properties prop = config.load();
        if (prop != null) {
            EditText edt;
            for (Object i : prop.keySet()) {
                int id = Integer.parseInt(i.toString());
                edt = (EditText) findViewById(id);
                edt.setText(prop.get(i).toString());
            }
        }
        System.gc();
    }

    public void calculate(View view) {
        calculateValue();
        saveConfig();
    }

    private void saveConfig() {
        Properties prop = new Properties();
        int[] ids = {R.id.txtKmGas, R.id.txtKmEtanol, R.id.txtValueGas, R.id.txtValueEtanol, R.id.txtDiscount};
        for (int id : ids) {
            prop.put(""+id, getValue((EditText)findViewById(id)).toString());
        }
        config.set(prop);
    }


    private void calculateValue() {
        int[] ids = {R.id.txtKmGas, R.id.txtKmEtanol, R.id.txtValueGas, R.id.txtValueEtanol, R.id.txtDiscount};
        if (checkValue(ids)) {

            Number kmEtanol = getValue(txtKmEtanol);
            Number kmGas = getValue(txtKmGas);
            Number valueGas = getValue(txtValueGas);
            Number valueEtanol = getValue(txtValueEtanol);
            Number discount = getValue(txtDiscount);

            if (discount.doubleValue() > 0) {
                valueGas = applyDiscount(valueGas, discount);
                valueEtanol = applyDiscount(valueEtanol, discount);

                txtValueDescGas.setText(valueGas.toString());
                txtValueDescEtanol.setText(valueEtanol.toString());
            }

            Double deltaEtanol = kmEtanol.doubleValue() / valueEtanol.doubleValue();
            Double deltaGas = kmGas.doubleValue() / valueGas.doubleValue();

            TextView txtResult = (TextView)findViewById(R.id.lblResult);
            if (deltaEtanol >= deltaGas) {
                txtResult.setText(R.string.etanol);
            } else {
                txtResult.setText(R.string.gas);
            }
        }
        System.gc();
    }

    private Double applyDiscount(Number value, Number discount) {
        return value.doubleValue() * ((100.0 - discount.doubleValue()) / 100.0);
    }

    private Number getValue(EditText txt) {
        String value = txt.getText().toString();
        try {
            value = value.replaceAll(",", ".");
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            try {
                return Integer.parseInt(value);
            } catch  (NumberFormatException e2) {
                return 0;
            }
        }
    }

    private boolean checkValue(int[] ids) {
        EditText t;
        String value;
        for (int i : ids) {
            t = (EditText)findViewById(i);
            value = t.getText().toString();
            try {
                value = value.replaceAll(",", ".");
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                try {
                    Integer.parseInt(value);
                } catch  (NumberFormatException e2) {
                    return false;
                }
            }
        }
        System.gc();
        return true;
    }

}
