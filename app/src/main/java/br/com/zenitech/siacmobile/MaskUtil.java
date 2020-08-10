package br.com.zenitech.siacmobile;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class MaskUtil {

    private static final String CPFMask = "###.###.###-##";
    private static final String CNPJMask = "##.###.###/####-##";
    private static final String LOGINMask = "##-##################";

    public static String unmask(String s) {
        /*if (s.length() <= 1) {
            //return s.replaceAll("[^0-9]*", "");
        } else {
            return s;
        }

        String c[] = s.split("-");
        String str = c[0] + c[1];
        return str;
        */

        String CurrentString = s;

        Log.e("LOGIN TOTAL: ", String.valueOf(s.length()));

        if (s.length() > 3) {
            try {
                String[] separated = CurrentString.split("-");
                s = separated[0] + separated[1];

                Log.e("LOGIN TXT0: ", s);
            } catch (Exception e) {
                //e.printStackTrace();

                Log.e("LOGIN TXT1: ", s);
            }

            //return s.replaceAll("[^0-9]*", "");
        } else {
            try {
                Log.e("LOGIN TXT2: ", s);
            } catch (Exception e) {
                //e.printStackTrace();

                Log.e("LOGIN TXT3: ", s);
            }
        }

        return s;
    }

    private static String getDefaultMask(String str) {
        String defaultMask = LOGINMask;
        /*if (str.length() == 14) {
            defaultMask = CNPJMask;
        }*/
        return defaultMask;
    }

    public enum MaskType {
        CPF,
        CNPJ,
        LOGINMask
    }

    public static TextWatcher insert(final EditText editText, final MaskType maskType) {
        return new TextWatcher() {

            boolean isUpdating;
            String oldValue = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = MaskUtil.unmask(s.toString());
                String mask;
                switch (maskType) {
                    case CPF:
                        mask = CPFMask;
                        break;
                    case CNPJ:
                        mask = CNPJMask;
                        break;
                    case LOGINMask:
                        mask = LOGINMask;
                        break;
                    default:
                        mask = getDefaultMask(value);
                        break;
                }

                String maskAux = "";
                if (isUpdating) {
                    oldValue = value;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && value.length() > oldValue.length()) || (m != '#' && value.length() < oldValue.length() && value.length() != i)) {
                        maskAux += m;
                        continue;
                    }

                    try {
                        maskAux += value.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(maskAux);
                editText.setSelection(maskAux.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }
}
