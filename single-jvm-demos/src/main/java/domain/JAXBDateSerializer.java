package domain;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JAXBDateSerializer extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }

    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }
}
