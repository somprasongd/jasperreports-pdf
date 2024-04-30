package com.github.somprasongd.jasperreports.pdf.dto;

import com.github.somprasongd.jasperreports.pdf.util.DateTime;
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
                return DateTime.parseDate(value);
            case ("time"):
                Date tm = DateTime.parseTime(value);
                if (tm == null) {
                    return null;
                }
                // if use java.util.Date it will be cut time in query and set to 00:00:00
                return new java.sql.Time(tm.getTime());
            case ("timestamp"):
                Date dt = DateTime.parseRFC3339Date(value);
                if (dt == null) {
                    return null;
                }
                // if use java.util.Date it will be cut time in query and set to 00:00:00
                return new java.sql.Timestamp(dt.getTime());
            case ("bool"):
                return Boolean.parseBoolean(value);
            case ("array_str"):
                String[] values = value.split(",");
                List<String> strs = new ArrayList<String>();
                Collections.addAll(strs, values);
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
