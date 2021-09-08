package com.insrb.app.insurance;

import com.insrb.app.exception.SearchException;
import com.insrb.app.util.InsuStringUtil;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class BldRgstService {

    /**
     *
     */
    private static final String APIS_DATA_GO_KR_SERVICE_KEY =
            "uc6vMDUub4APkNN4uC0WT3N19fZaUBPVKIfgiiJmOBXVn+Imupb6vOaFNvI5GYwDAMVdGqvGNbojGRcnH4xV2w==";
    /**
     *
     */
    private static final String JUSO_GO_KR_URL = "https://www.juso.go.kr/addrlink/addrLinkApi.do";
    private static final String CONFIRM_KEY = "U01TX0FVVEgyMDIxMDMzMTE1MzA1NTExMDk5Mzc=";

    /**
     * Spec : https://www.juso.go.kr/addrlink/devAddrLinkRequestGuide.do?menu=roadApi
     *
     * @param search
     * @return JSONObject
     * @throws SearchException
     */
    public Map<String, Object> getJusoList(String search) throws SearchException {
        log.debug("getJusoList:" + search);
        HttpResponse<JsonNode> res = Unirest
                .post(JUSO_GO_KR_URL)
                .field("keyword", search)
                .field("confmKey", CONFIRM_KEY)
                .field("resultType", "json")
                .field("currentPage", "1")
                .field("countPerPage", "1000")
                .field("addressDong", "")
                .field("addressHo", "")
                .asJson();

        JSONObject json = res.getBody().getObject();

        String rep = json.toString().replaceAll("&amp;", "&");

        JSONObject json_rep = new JSONObject(rep);

        log.debug(json_rep.toString());
        if(Objects.isNull(json_rep)) throw new SearchException("응답이없습니다");
        String errCode = json_rep.getJSONObject("results").getJSONObject("common").getString("errorCode");
        if(!InsuStringUtil.Equals(errCode, "0")){
            String errorMessage = json_rep.getJSONObject("results").getJSONObject("common").getString("errorMessage");
            throw new SearchException(errorMessage);
        }
        String totalCount = json_rep.getJSONObject("results").getJSONObject("common").getString("totalCount");
        if(InsuStringUtil.Equals(totalCount, "0")){
            throw new SearchException("조회된 데이터가 없습니다.");
        }
        return json_rep.toMap();
    }

    /**
     * 건축물대장 기본개요API
     * Spec: https://www.data.go.kr/data/15044713/openapi.do
     * TODO: kong.unirest.json.JSONObject가 아닌
     * org.json.JSONObject라 맘에 안듬. 나중에 수정할것. 해결되면 org.json pom에서 제거할 것.
     * 일단 의존성 최소화를 위해 이 클래스안에서만 사용하기위해서 Map으로 리턴
     *
     * sigunguCd = juso.admCd 앞 5 자리
     * bjdongCd = juso.admCd 나머지 5자리
     * bun = juso.lnbrMnnm
     * ji =  juso.lnbrSlno
     * 건물명 = juso.bdNm
     *
     * @return  Map<String,Object>
     */
    public Map<String, Object> getBrBasisOulnInfo(String sigunguCd, String bjdongCd, int bun, int ji) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);
        log.debug("getBrBasisOulnInfo:" + sigunguCd + "," + bjdongCd + "," + str_bun + "," + str_ji);

        HttpResponse<String> res = Unirest
                .get("http://apis.data.go.kr/1613000/BldRgstService_v2/getBrBasisOulnInfo")
                .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY)
                .queryString("sigunguCd", sigunguCd)
                .queryString("bjdongCd", bjdongCd)
                .queryString("bun", str_bun)
                .queryString("ji", str_ji)
                .queryString("numOfRows", "1000")
                .queryString("pageNo", "1")
                .asString();
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());
        return jObject.toMap();
    }

    /**
     * 건축물대장 총괄표제부API
     * Spec: https://www.data.go.kr/data/15044713/openapi.do
     *
     * sigunguCd = juso.admCd 앞 5 자리
     * bjdongCd = juso.admCd 나머지 5자리
     * bun = juso.lnbrMnnm
     * ji =  juso.lnbrSlno
     * 건물명 = juso.bdNm
     *
     * @return  Map<String,Object>
     */
    public Map<String, Object> getBrRecapTitleInfo(String sigunguCd, String bjdongCd, int bun, int ji) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);
        log.debug("getHouseCoverInfo:" + sigunguCd + "," + bjdongCd + "," + str_bun + "," + str_ji);

        HttpResponse<String> res = Unirest
                .get("http://apis.data.go.kr/1613000/BldRgstService_v2/getBrRecapTitleInfo")
                .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY)
                .queryString("sigunguCd", sigunguCd)
                .queryString("bjdongCd", bjdongCd)
                .queryString("bun", str_bun)
                .queryString("ji", str_ji)
                .queryString("numOfRows", "1000")
                .queryString("pageNo", "1")
                .asString();
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());
        return jObject.toMap();
    }


    /**
     * 건축물대장 표제부API
     * Spec: https://www.data.go.kr/data/15044713/openapi.do
     *
     * sigunguCd = juso.admCd 앞 5 자리
     * bjdongCd = juso.admCd 나머지 5자리
     * bun = juso.lnbrMnnm
     * ji =  juso.lnbrSlno
     * 건물명 = juso.bdNm
     *
     * @return  Map<String,Object>
     */
    public Map<String, Object> getBrTitleInfo(String sigunguCd, String bjdongCd, int bun, int ji) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);
        log.debug("getBrTitleInfo:" + sigunguCd + "," + bjdongCd + "," + str_bun + "," + str_ji);

        HttpResponse<String> res = Unirest
                .get("http://apis.data.go.kr/1613000/BldRgstService_v2/getBrTitleInfo")
                .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY)
                .queryString("sigunguCd", sigunguCd)
                .queryString("bjdongCd", bjdongCd)
                .queryString("bun", str_bun)
                .queryString("ji", str_ji)
                .queryString("numOfRows", "1000")
                .queryString("pageNo", "1")
                .asString();
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());
        return jObject.toMap();
    }


    /**
     * 건축물대장 층별개요API
     *
     * sigunguCd = juso.admCd 앞 5 자리
     * bjdongCd = juso.admCd 나머지 5자리
     * bun = juso.lnbrMnnm
     * ji =  juso.lnbrSlno
     * 건물명 = juso.bdNm
     *
     * @return  Map<String,Object>
     */
    public Map<String, Object> getBrFlrOulnInfo(String sigunguCd, String bjdongCd, int bun, int ji) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);
        log.debug("getBrFlrOulnInfo:" + sigunguCd + "," + bjdongCd + "," + str_bun + "," + str_ji);


        HttpResponse<String> res = Unirest
                .get("http://apis.data.go.kr/1613000/BldRgstService_v2/getBrFlrOulnInfo")
                .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY)
                .queryString("sigunguCd", sigunguCd)
                .queryString("bjdongCd", bjdongCd)
                .queryString("bun", str_bun)
                .queryString("ji", str_ji)
                .queryString("numOfRows", "1000")
                .queryString("pageNo", "1")
                .asString();
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());
        return jObject.toMap();
    }


    /**
     * 건축물대장 전유공용면적API
     * Spec: https://www.data.go.kr/data/15044713/openapi.do
     * TODO: kong.unirest.json.JSONObject가 아닌
     * org.json.JSONObject라 맘에 안듬. 나중에 수정할것. 해결되면 org.json pom에서 제거할 것.
     * 일단 의존성 최소화를 위해 이 클래스안에서만 사용하기위해서 Map으로 리턴
     *
     * sigunguCd = juso.admCd 앞 5 자리
     * bjdongCd = juso.admCd 나머지 5자리
     * bun = juso.lnbrMnnm
     * ji =  juso.lnbrSlno
     * 건물명 = juso.bdNm
     *
     * @return  Map<String,Object>
     */
    public Map<String, Object> getBrExposPubuseAreaInfo(String sigunguCd, String bjdongCd, int bun, int ji, String dongnm, String honm) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);
        log.debug("getBrExposPubuseAreaInfo:" + sigunguCd + "," + bjdongCd + "," + str_bun + "," + str_ji);

        HttpResponse<String> res = Unirest
                .get("http://apis.data.go.kr/1613000/BldRgstService_v2/getBrExposPubuseAreaInfo")
                .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY)
                .queryString("sigunguCd", sigunguCd)
                .queryString("bjdongCd", bjdongCd)
                .queryString("bun", str_bun)
                .queryString("ji", str_ji)
                .queryString("dongNm", dongnm)
                .queryString("hoNm", honm)
                .queryString("numOfRows", "1000")
                .queryString("pageNo", "1")
                .asString();
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());
        return jObject.toMap();
    }


    /**
     * 건축물대장 전유부API
     * Spec: https://www.data.go.kr/data/15044713/openapi.do
     * TODO: kong.unirest.json.JSONObject가 아닌
     * org.json.JSONObject라 맘에 안듬. 나중에 수정할것. 해결되면 org.json pom에서 제거할 것.
     * 일단 의존성 최소화를 위해 이 클래스안에서만 사용하기위해서 Map으로 리턴
     *
     * sigunguCd = juso.admCd 앞 5 자리
     * bjdongCd = juso.admCd 나머지 5자리
     * bun = juso.lnbrMnnm
     * ji =  juso.lnbrSlno
     * 건물명 = juso.bdNm
     *
     * @return  Map<String,Object>
     */
    public Map<String, Object> getBrExposInfo(String sigunguCd, String bjdongCd, int bun, int ji, String dongnm, String honm) {
        String str_bun = String.format("%04d", bun);
        String str_ji = String.format("%04d", ji);
        log.debug("getBrExposInfo:" + sigunguCd + "," + bjdongCd + "," + str_bun + "," + str_ji);

        HttpResponse<String> res = Unirest
                .get("http://apis.data.go.kr/1613000/BldRgstService_v2/getBrExposInfo")
                .queryString("ServiceKey", APIS_DATA_GO_KR_SERVICE_KEY)
                .queryString("sigunguCd", sigunguCd)
                .queryString("bjdongCd", bjdongCd)
                .queryString("bun", str_bun)
                .queryString("ji", str_ji)
                .queryString("dongNm", dongnm)
                .queryString("hoNm", honm)
                .queryString("numOfRows", "1000")
                .queryString("pageNo", "1")
                .asString();
        org.json.JSONObject jObject = XML.toJSONObject(res.getBody());
        return jObject.toMap();
    }

}
