package com.github.somprasongd.jasperreports.pdf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ParameterDto {

    @NotBlank
    private String name;

    @NotBlank
    @Value("${some.key:string}")
    private String type; // string, integer, number, date, time, timestamp, bool, array_str, array_int

    @NotNull
    private String value;

    public Object getConvertedValue() {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return "";
        }
        switch (type) {
            case ("integer"):
                return Integer.parseInt(value);
            case ("number"):
                return Double.parseDouble(value);
            case ("date"):
                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    date = sdf.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date;
            case ("time"):
                Date time = null;
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.US);
                try {
                    time = sdf2.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return time;
            case ("timestamp"):
                Date dateTime = null;
                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                try {
                    dateTime = sdf3.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return dateTime;
            case ("bool"):
                return Boolean.parseBoolean(value);
            case ("array_str"):
                String[] values = value.split(",");
                List<String> strs = new ArrayList<>();
                for (String val :
                        values) {
                    strs.add(val);
                }
                return strs;
            case ("array_int"):
                String[] values2 = value.split(",");
                List<Integer> ints = new ArrayList<>();
                for (String val2 :
                        values2) {
                    ints.add(new Integer(val2));
                }
                return ints;
            default:
                return value;
        }
    }
}
