package ng.siteworx.partner.serviceprovider.artisan.constants

object Constants {

    enum class HighestCertificate{
        MSC,
        DEGREE,
        HND,
        DIPLOMA,
        NCE,
        PROFICIENT,
        O_LEVEL,
        PSLC,
        NONE
    }

    enum class Category{
        CAPENTRY,
        TILING,
        MASONRY,
        ELECTRICAN,
        PAINTING,
        IRON_BENDING,
        AIRCONDITIONAL_TECHNICIAN,
        POP,
        INTERIOR_DECORATION,
        NONE
    }

    enum class Gender{
        MALE,
        FEMALE,
        NON_BINARY
    }
    enum class AccountType{
        SAVING,
        CURRENT,
        DOMICILIARY,
    }
    enum class SatisfactionType{
        SATISFY,
        NOT_SATISFY,
        VERY_SATISFY
    }
}