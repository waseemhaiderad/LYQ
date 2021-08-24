package lnq.com.lnq.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegionUtils {

    static String continent = "{\n" +
            "  \"AD\": \"Europe\",\n" +
            "  \"AE\": \"Asia\",\n" +
            "  \"AF\": \"Asia\",\n" +
            "  \"AG\": \"North America\",\n" +
            "  \"AI\": \"North America\",\n" +
            "  \"AL\": \"Europe\",\n" +
            "  \"AM\": \"Asia\",\n" +
            "  \"AN\": \"North America\",\n" +
            "  \"AO\": \"Africa\",\n" +
            "  \"AQ\": \"Antarctica\",\n" +
            "  \"AR\": \"South America\",\n" +
            "  \"AS\": \"Australia\",\n" +
            "  \"AT\": \"Europe\",\n" +
            "  \"AU\": \"Australia\",\n" +
            "  \"AW\": \"North America\",\n" +
            "  \"AZ\": \"Asia\",\n" +
            "  \"BA\": \"Europe\",\n" +
            "  \"BB\": \"North America\",\n" +
            "  \"BD\": \"Asia\",\n" +
            "  \"BE\": \"Europe\",\n" +
            "  \"BF\": \"Africa\",\n" +
            "  \"BG\": \"Europe\",\n" +
            "  \"BH\": \"Asia\",\n" +
            "  \"BI\": \"Africa\",\n" +
            "  \"BJ\": \"Africa\",\n" +
            "  \"BM\": \"North America\",\n" +
            "  \"BN\": \"Asia\",\n" +
            "  \"BO\": \"South America\",\n" +
            "  \"BR\": \"South America\",\n" +
            "  \"BS\": \"North America\",\n" +
            "  \"BT\": \"Asia\",\n" +
            "  \"BW\": \"Africa\",\n" +
            "  \"BY\": \"Europe\",\n" +
            "  \"BZ\": \"North America\",\n" +
            "  \"CA\": \"North America\",\n" +
            "  \"CC\": \"Asia\",\n" +
            "  \"CD\": \"Africa\",\n" +
            "  \"CF\": \"Africa\",\n" +
            "  \"CG\": \"Africa\",\n" +
            "  \"CH\": \"Europe\",\n" +
            "  \"CI\": \"Africa\",\n" +
            "  \"CK\": \"Australia\",\n" +
            "  \"CL\": \"South America\",\n" +
            "  \"CM\": \"Africa\",\n" +
            "  \"CN\": \"Asia\",\n" +
            "  \"CO\": \"South America\",\n" +
            "  \"CR\": \"North America\",\n" +
            "  \"CU\": \"North America\",\n" +
            "  \"CV\": \"Africa\",\n" +
            "  \"CX\": \"Asia\",\n" +
            "  \"CY\": \"Asia\",\n" +
            "  \"CZ\": \"Europe\",\n" +
            "  \"DE\": \"Europe\",\n" +
            "  \"DJ\": \"Africa\",\n" +
            "  \"DK\": \"Europe\",\n" +
            "  \"DM\": \"North America\",\n" +
            "  \"DO\": \"North America\",\n" +
            "  \"DZ\": \"Africa\",\n" +
            "  \"EC\": \"South America\",\n" +
            "  \"EE\": \"Europe\",\n" +
            "  \"EG\": \"Africa\",\n" +
            "  \"EH\": \"Africa\",\n" +
            "  \"ER\": \"Africa\",\n" +
            "  \"ES\": \"Europe\",\n" +
            "  \"ET\": \"Africa\",\n" +
            "  \"FI\": \"Europe\",\n" +
            "  \"FJ\": \"Australia\",\n" +
            "  \"FK\": \"South America\",\n" +
            "  \"FM\": \"Australia\",\n" +
            "  \"FO\": \"Europe\",\n" +
            "  \"FR\": \"Europe\",\n" +
            "  \"GA\": \"Africa\",\n" +
            "  \"GB\": \"Europe\",\n" +
            "  \"GD\": \"North America\",\n" +
            "  \"GE\": \"Asia\",\n" +
            "  \"GF\": \"South America\",\n" +
            "  \"GG\": \"Europe\",\n" +
            "  \"GH\": \"Africa\",\n" +
            "  \"GI\": \"Europe\",\n" +
            "  \"GL\": \"North America\",\n" +
            "  \"GM\": \"Africa\",\n" +
            "  \"GN\": \"Africa\",\n" +
            "  \"GP\": \"North America\",\n" +
            "  \"GQ\": \"Africa\",\n" +
            "  \"GR\": \"Europe\",\n" +
            "  \"GS\": \"Antarctica\",\n" +
            "  \"GT\": \"North America\",\n" +
            "  \"GU\": \"Australia\",\n" +
            "  \"GW\": \"Africa\",\n" +
            "  \"GY\": \"South America\",\n" +
            "  \"HK\": \"Asia\",\n" +
            "  \"HN\": \"North America\",\n" +
            "  \"HR\": \"Europe\",\n" +
            "  \"HT\": \"North America\",\n" +
            "  \"HU\": \"Europe\",\n" +
            "  \"ID\": \"Asia\",\n" +
            "  \"IE\": \"Europe\",\n" +
            "  \"IL\": \"Asia\",\n" +
            "  \"IM\": \"Europe\",\n" +
            "  \"IN\": \"Asia\",\n" +
            "  \"IO\": \"Asia\",\n" +
            "  \"IQ\": \"Asia\",\n" +
            "  \"IR\": \"Asia\",\n" +
            "  \"IS\": \"Europe\",\n" +
            "  \"IT\": \"Europe\",\n" +
            "  \"JE\": \"Europe\",\n" +
            "  \"JM\": \"North America\",\n" +
            "  \"JO\": \"Asia\",\n" +
            "  \"JP\": \"Asia\",\n" +
            "  \"KE\": \"Africa\",\n" +
            "  \"KG\": \"Asia\",\n" +
            "  \"KH\": \"Asia\",\n" +
            "  \"KI\": \"Australia\",\n" +
            "  \"KM\": \"Africa\",\n" +
            "  \"KN\": \"North America\",\n" +
            "  \"KP\": \"Asia\",\n" +
            "  \"KR\": \"Asia\",\n" +
            "  \"KW\": \"Asia\",\n" +
            "  \"KY\": \"North America\",\n" +
            "  \"KZ\": \"Asia\",\n" +
            "  \"LA\": \"Asia\",\n" +
            "  \"LB\": \"Asia\",\n" +
            "  \"LC\": \"North America\",\n" +
            "  \"LI\": \"Europe\",\n" +
            "  \"LK\": \"Asia\",\n" +
            "  \"LR\": \"Africa\",\n" +
            "  \"LS\": \"Africa\",\n" +
            "  \"LT\": \"Europe\",\n" +
            "  \"LU\": \"Europe\",\n" +
            "  \"LV\": \"Europe\",\n" +
            "  \"LY\": \"Africa\",\n" +
            "  \"MA\": \"Africa\",\n" +
            "  \"MC\": \"Europe\",\n" +
            "  \"MD\": \"Europe\",\n" +
            "  \"ME\": \"Europe\",\n" +
            "  \"MG\": \"Africa\",\n" +
            "  \"MH\": \"Australia\",\n" +
            "  \"MK\": \"Europe\",\n" +
            "  \"ML\": \"Africa\",\n" +
            "  \"MM\": \"Asia\",\n" +
            "  \"MN\": \"Asia\",\n" +
            "  \"MO\": \"Asia\",\n" +
            "  \"MP\": \"Australia\",\n" +
            "  \"MQ\": \"North America\",\n" +
            "  \"MR\": \"Africa\",\n" +
            "  \"MS\": \"North America\",\n" +
            "  \"MT\": \"Europe\",\n" +
            "  \"MU\": \"Africa\",\n" +
            "  \"MV\": \"Asia\",\n" +
            "  \"MW\": \"Africa\",\n" +
            "  \"MX\": \"North America\",\n" +
            "  \"MY\": \"Asia\",\n" +
            "  \"MZ\": \"Africa\",\n" +
            "  \"NA\": \"Africa\",\n" +
            "  \"NC\": \"Australia\",\n" +
            "  \"NE\": \"Africa\",\n" +
            "  \"NF\": \"Australia\",\n" +
            "  \"NG\": \"Africa\",\n" +
            "  \"NI\": \"North America\",\n" +
            "  \"NL\": \"Europe\",\n" +
            "  \"NO\": \"Europe\",\n" +
            "  \"NP\": \"Asia\",\n" +
            "  \"NR\": \"Australia\",\n" +
            "  \"NU\": \"Australia\",\n" +
            "  \"NZ\": \"Australia\",\n" +
            "  \"OM\": \"Asia\",\n" +
            "  \"PA\": \"North America\",\n" +
            "  \"PE\": \"South America\",\n" +
            "  \"PF\": \"Australia\",\n" +
            "  \"PG\": \"Australia\",\n" +
            "  \"PH\": \"Asia\",\n" +
            "  \"PK\": \"Asia\",\n" +
            "  \"PL\": \"Europe\",\n" +
            "  \"PM\": \"North America\",\n" +
            "  \"PN\": \"Australia\",\n" +
            "  \"PR\": \"North America\",\n" +
            "  \"PS\": \"Asia\",\n" +
            "  \"PT\": \"Europe\",\n" +
            "  \"PW\": \"Australia\",\n" +
            "  \"PY\": \"South America\",\n" +
            "  \"QA\": \"Asia\",\n" +
            "  \"RE\": \"Africa\",\n" +
            "  \"RO\": \"Europe\",\n" +
            "  \"RS\": \"Europe\",\n" +
            "  \"RU\": \"Europe\",\n" +
            "  \"RW\": \"Africa\",\n" +
            "  \"SA\": \"Asia\",\n" +
            "  \"SB\": \"Australia\",\n" +
            "  \"SC\": \"Africa\",\n" +
            "  \"SD\": \"Africa\",\n" +
            "  \"SE\": \"Europe\",\n" +
            "  \"SG\": \"Asia\",\n" +
            "  \"SH\": \"Africa\",\n" +
            "  \"SI\": \"Europe\",\n" +
            "  \"SJ\": \"Europe\",\n" +
            "  \"SK\": \"Europe\",\n" +
            "  \"SL\": \"Africa\",\n" +
            "  \"SM\": \"Europe\",\n" +
            "  \"SN\": \"Africa\",\n" +
            "  \"SO\": \"Africa\",\n" +
            "  \"SR\": \"South America\",\n" +
            "  \"ST\": \"Africa\",\n" +
            "  \"SV\": \"North America\",\n" +
            "  \"SY\": \"Asia\",\n" +
            "  \"SZ\": \"Africa\",\n" +
            "  \"TC\": \"North America\",\n" +
            "  \"TD\": \"Africa\",\n" +
            "  \"TF\": \"Antarctica\",\n" +
            "  \"TG\": \"Africa\",\n" +
            "  \"TH\": \"Asia\",\n" +
            "  \"TJ\": \"Asia\",\n" +
            "  \"TK\": \"Australia\",\n" +
            "  \"TM\": \"Asia\",\n" +
            "  \"TN\": \"Africa\",\n" +
            "  \"TO\": \"Australia\",\n" +
            "  \"TR\": \"Asia\",\n" +
            "  \"TT\": \"North America\",\n" +
            "  \"TV\": \"Australia\",\n" +
            "  \"TW\": \"Asia\",\n" +
            "  \"TZ\": \"Africa\",\n" +
            "  \"UA\": \"Europe\",\n" +
            "  \"UG\": \"Africa\",\n" +
            "  \"US\": \"North America\",\n" +
            "  \"UY\": \"South America\",\n" +
            "  \"UZ\": \"Asia\",\n" +
            "  \"VC\": \"North America\",\n" +
            "  \"VE\": \"South America\",\n" +
            "  \"VG\": \"North America\",\n" +
            "  \"VI\": \"North America\",\n" +
            "  \"VN\": \"Asia\",\n" +
            "  \"VU\": \"Australia\",\n" +
            "  \"WF\": \"Australia\",\n" +
            "  \"WS\": \"Australia\",\n" +
            "  \"YE\": \"Asia\",\n" +
            "  \"YT\": \"Africa\",\n" +
            "  \"ZA\": \"Africa\",\n" +
            "  \"ZM\": \"Africa\",\n" +
            "  \"ZW\": \"Africa\"\n" +
            "}";


    public static String getContinentName(String key) {
        try {
            JSONObject jsonObject = new JSONObject(continent);
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

}
