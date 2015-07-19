package au.com.dius.pact.consumer;

import nl.flotsam.xeger.Xeger;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONArray;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PactDslJsonArray extends DslPart {

    private final JSONArray body;
    private boolean wildCard;

    public PactDslJsonArray() {
		this("", null, false);
	}
	
    public PactDslJsonArray(String root, DslPart parent) {
        this(root, parent, false);
    }

    public PactDslJsonArray(String root, DslPart parent, boolean wildCard) {
        super(parent, root);
        this.wildCard = wildCard;
        body = new JSONArray();
    }

    public DslPart closeArray() {
        parent.putArray(this);
        return parent;
    }

    @Override
    public PactDslJsonBody arrayLike(String name) {
        throw new UnsupportedOperationException("use the arrayLike() form");
    }

    @Override
    public PactDslJsonBody arrayLike() {
        Map<String, Object> matcher = new HashMap<String, Object>();
        matcher.put("match", "type");
        matchers.put(root + appendArrayIndex(1), matcher);
        PactDslJsonArray parent = new PactDslJsonArray(root, this, true);
        return new PactDslJsonBody(".", parent);
    }

    @Override
    public PactDslJsonBody eachLike(String name) {
        throw new UnsupportedOperationException("use the eachLike() form");
    }

    @Override
    public PactDslJsonBody eachLike() {
        matchers.put(root + appendArrayIndex(1), matchMin(0));
        PactDslJsonArray parent = new PactDslJsonArray(root, this, true);
        return new PactDslJsonBody(".", parent);
    }

    @Override
    public PactDslJsonBody minArrayLike(String name, Integer size) {
        throw new UnsupportedOperationException("use the minArrayLike(Integer size) form");
    }

    @Override
    public PactDslJsonBody minArrayLike(Integer size) {
        matchers.put(root + appendArrayIndex(1), matchMin(size));
        PactDslJsonArray parent = new PactDslJsonArray("", this, true);
        return new PactDslJsonBody(".", parent);
    }

    @Override
    public PactDslJsonBody maxArrayLike(String name, Integer size) {
        throw new UnsupportedOperationException("use the maxArrayLike(Integer size) form");
    }

    @Override
    public PactDslJsonBody maxArrayLike(Integer size) {
        matchers.put(root + appendArrayIndex(1), matchMax(size));
        PactDslJsonArray parent = new PactDslJsonArray("", this, true);
        return new PactDslJsonBody(".", parent);
    }

    protected void putObject(DslPart object) {
        for(String matcherName: object.matchers.keySet()) {
            matchers.put(root + appendArrayIndex(1) + matcherName, object.matchers.get(matcherName));
        }
        body.put(object.getBody());
    }

    protected void putArray(DslPart object) {
        for(String matcherName: object.matchers.keySet()) {
            matchers.put(root + appendArrayIndex(1) + matcherName, object.matchers.get(matcherName));
        }
        body.put(object.getBody());
    }

    @Override
    protected Object getBody() {
        return body;
    }

    public PactDslJsonArray stringValue(String value) {
        body.put(value);
        return this;
    }

    public PactDslJsonArray string(String value) {
        return stringValue(value);
    }

    public PactDslJsonArray numberValue(Number value) {
        body.put(value);
        return this;
    }

    public PactDslJsonArray number(Number value) {
        return numberValue(value);
    }

    public PactDslJsonArray booleanValue(Boolean value) {
        body.put(value);
        return this;
    }

    public PactDslJsonArray stringType() {
        body.put(RandomStringUtils.randomAlphabetic(20));
        matchers.put(root + appendArrayIndex(0), matchType());
        return this;
    }

    public PactDslJsonArray numberType() {
        return numberType(Long.parseLong(RandomStringUtils.randomNumeric(10)));
    }

    public PactDslJsonArray numberType(Number number) {
        body.put(number);
        matchers.put(root + appendArrayIndex(0), matchType("type"));
        return this;
    }

    public PactDslJsonArray integerType() {
        return integerType(Long.parseLong(RandomStringUtils.randomNumeric(10)));
    }

    public PactDslJsonArray integerType(Long number) {
        body.put(number);
        matchers.put(root + appendArrayIndex(0), matchType("integer"));
        return this;
    }

    public PactDslJsonArray realType() {
        return realType(Double.parseDouble(RandomStringUtils.randomNumeric(10)) / 100.0);
    }

    public PactDslJsonArray realType(Double number) {
        body.put(number);
        matchers.put(root + appendArrayIndex(0), matchType("real"));
        return this;
    }

    public PactDslJsonArray booleanType(String name) {
        body.put(true);
        matchers.put(root + appendArrayIndex(0), matchType());
        return this;
    }

    public PactDslJsonArray stringMatcher(String regex, String value) {
        if (!value.matches(regex)) {
            throw new InvalidMatcherException("Example \"" + value + "\" does not match regular expression \"" +
                regex + "\"");
        }
        body.put(value);
        matchers.put(root + appendArrayIndex(0), regexp(regex));
        return this;
    }

    public PactDslJsonArray stringMatcher(String regex) {
        stringMatcher(regex, new Xeger(regex).generate());
        return this;
    }

    public PactDslJsonArray timestamp() {
        body.put(DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
        matchers.put(root + appendArrayIndex(0), matchTimestamp(DateFormatUtils.ISO_DATETIME_FORMAT.getPattern()));
        return this;
    }

    public PactDslJsonArray timestamp(String format) {
        FastDateFormat instance = FastDateFormat.getInstance(format);
        body.put(instance.format(new Date()));
        matchers.put(root + appendArrayIndex(0), matchTimestamp(format));
        return this;
    }

    public PactDslJsonArray date() {
        body.put(DateFormatUtils.ISO_DATE_FORMAT.format(new Date()));
        matchers.put(root + appendArrayIndex(0), matchDate(DateFormatUtils.ISO_DATE_FORMAT.getPattern()));
        return this;
    }

    public PactDslJsonArray date(String format) {
        FastDateFormat instance = FastDateFormat.getInstance(format);
        body.put(instance.format(new Date()));
        matchers.put(root + appendArrayIndex(0), matchDate(format));
        return this;
    }

    public PactDslJsonArray time() {
        body.put(DateFormatUtils.ISO_TIME_FORMAT.format(new Date()));
        matchers.put(root + appendArrayIndex(0), matchTime(DateFormatUtils.ISO_TIME_FORMAT.getPattern()));
        return this;
    }

    public PactDslJsonArray time(String format) {
        FastDateFormat instance = FastDateFormat.getInstance(format);
        body.put(instance.format(new Date()));
        matchers.put(root + appendArrayIndex(0), matchTime(format));
        return this;
    }

    public PactDslJsonArray ipAddress() {
        body.put("127.0.0.1");
        matchers.put(root + appendArrayIndex(0), regexp("(\\d{1,3}\\.)+\\d{1,3}"));
        return this;
    }

    public PactDslJsonBody object(String name) {
        throw new UnsupportedOperationException("use the object() form");
    }

    public PactDslJsonBody object() {
        return new PactDslJsonBody(".", this);
    }

    @Override
    public DslPart closeObject() {
        throw new UnsupportedOperationException("can't call closeObject on an Array");
    }

    public PactDslJsonArray array(String name) {
        throw new UnsupportedOperationException("use the array() form");
    }

    public PactDslJsonArray array() {
        return new PactDslJsonArray("", this);
    }

    public PactDslJsonArray id() {
        body.put(RandomStringUtils.randomNumeric(10));
        matchers.put(root + appendArrayIndex(0), matchType());
        return this;
    }

    public PactDslJsonArray hexValue() {
        return hexValue(RandomStringUtils.random(10, "0123456789abcdef"));
    }

    public PactDslJsonArray hexValue(String hexValue) {
        if (!hexValue.matches(HEXADECIMAL)) {
            throw new InvalidMatcherException("Example \"" + hexValue + "\" is not a hexadecimal value");
        }
        body.put(hexValue);
        matchers.put(root + appendArrayIndex(0), regexp("[0-9a-fA-F]+"));
        return this;
    }

    public PactDslJsonArray guid() {
        return guid(UUID.randomUUID().toString());
    }

    public PactDslJsonArray guid(String uuid) {
        if (!uuid.matches(GUID)) {
            throw new InvalidMatcherException("Example \"" + uuid + "\" is not a GUID");
        }
        body.put(uuid);
        matchers.put(root + appendArrayIndex(0), regexp("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        return this;
    }
	
	public PactDslJsonArray template(DslPart template) {
		putObject(template);
		return this;
	}
	
	public PactDslJsonArray template(DslPart template, int occurrences) {
		for(int i = 0; i < occurrences; i++) {
			template(template);	
		}
		return this;
	}
	
	@Override
	public String toString() {
		return body.toString();
	}

    private String appendArrayIndex(Integer offset) {
        String index = "*";
        if (!wildCard) {
            index = String.valueOf(body.length() - 1 + offset);
        }
        return "[" + index + "]";
    }
}
