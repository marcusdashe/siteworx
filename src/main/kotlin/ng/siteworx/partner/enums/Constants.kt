package ng.siteworx.partner.enums

    object SiteworxEnums{
    enum class UserType{
        ADMIN,
        CLIENT,
        ARTISAN,
        SUB_CONTRACTOR,
        PROFESSIONAL,
        MERCHANT
    }
    enum class HighestCertificate{
        PHD,
        MSC,
        DEGREE,
        HND,
        DIPLOMA,
        NCE,
        PROFICIENCY,
        O_LEVEL,
        PSLC,
        OTHERS
    }

    enum class Category{
        CARPENTRY,
        TILING,
        MASONRY,
        ELECTRICIAN,
        PAINTING,
        IRON_BENDING,
        AIRCONDITION_TECHNICIAN,
        POP,
        INTERIOR_DECORATION,
        NONE
    }

    enum class Gender{
        MALE,
        FEMALE,
        NONE
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

    enum class SubCategory{
        FREE,
        TRIAL,
        PAID,
    }
    }