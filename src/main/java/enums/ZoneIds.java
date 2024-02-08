package enums;

public enum ZoneIds {
    AUSTRALIA_DARWIN("Australia/Darwin"),
    AUSTRALIA_SYDNEY("Australia/Sydney"),
    AUSTRALIA_ARGENTINA_BUENOS_AIRES("America/Argentina/Buenos_Aires"),
    AFRICA_CAIRO("Africa/Cairo"),
    AMERICA_ANCHORAGE("America/Anchorage"),
    AMERICA_SAO_PAULO("America/Sao_Paulo"),
    ASIA_DHAKA("Asia/Dhaka"),
    AFRICA_HARARE("Africa/Harare"),
    AMERICA_ST_JOHNS("America/St_Johns"),
    AMERICA_CHICAGO("America/Chicago"),
    ASIA_SHANGHAI("Asia/Shanghai"),
    AFRICA_ADDIS_ABABA("Africa/Addis_Ababa"),
    EUROPE_PARIS("Europe/Paris"),
    AMERICA_INDIANA_INDIANAPOLIS("America/Indiana/Indianapolis"),
    ASIA_KOLKATA("Asia/Kolkata"),
    ASIA_TOKYO("Asia/Tokyo"),
    PACIFIC_APIA("Pacific/Apia"),
    ASIA_YEREVAN("Asia/Yerevan"),
    PACIFIC_AUCKLAND("Pacific/Auckland"),
    ASIA_KARACHI("Asia/Karachi"),
    AMERICA_PHOENIX("America/Phoenix"),
    AMERICA_PUERTO_RICO("America/Puerto_Rico"),
    AMERICA_LOS_ANGELES("America/Los_Angeles"),
    PACIFIC_GUADALCANAL("Pacific/Guadalcanal"),
    ASIA_HO_CHI_MINH("Asia/Ho_Chi_Minh");

    final String zoneId;

    ZoneIds(String zoneId){
        this.zoneId = zoneId;
    }

    public String getZoneId() {
        return zoneId;
    }
}
