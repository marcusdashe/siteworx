package ng.siteworx.partner.serviceprovider.artisan.service

import ng.siteworx.partner.enums.SiteworxEnums
import ng.siteworx.partner.serviceprovider.artisan.dto.ProfileDTO
import ng.siteworx.partner.serviceprovider.artisan.repo.ArtisanRepo
import ng.siteworx.partner.serviceprovider.artisan.repo.ProfileRepo
import ng.siteworx.partner.serviceprovider.sharedmodel.Profile
import ng.siteworx.partner.serviceprovider.sharedpayload.Message
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.name


@Service
class ProfileService(private val artisanService: ArtisanService, private val profileRepo: ProfileRepo, private val artisanRepo: ArtisanRepo) {

        var returnedProfile: Profile? = null

//
        fun mappingOfPayloadStrToTradeCategoryEnum(payloadStr: String): SiteworxEnums.Category {
                return when(payloadStr){
                        "carpentry" -> SiteworxEnums.Category.CARPENTRY
                        "tiling" -> SiteworxEnums.Category.TILING
                        "masonry" -> SiteworxEnums.Category.MASONRY
                        "electrician" -> SiteworxEnums.Category.ELECTRICIAN
                        "painting" -> SiteworxEnums.Category.PAINTING
                        "iron bending" -> SiteworxEnums.Category.IRON_BENDING
                        "air condition technician" -> SiteworxEnums.Category.AIRCONDITION_TECHNICIAN
                        "pop" -> SiteworxEnums.Category.POP
                        "interior decoration" -> SiteworxEnums.Category.INTERIOR_DECORATION
                        else -> SiteworxEnums.Category.NONE
                }
        }

        fun mappingOfPayloadStrToHighestCertificatesObtained(payloadStr: String): SiteworxEnums.HighestCertificate {
                return when(payloadStr){
                        "phd" -> SiteworxEnums.HighestCertificate.PHD
                        "msc" -> SiteworxEnums.HighestCertificate.MSC
                        "degree" -> SiteworxEnums.HighestCertificate.DEGREE
                        "hnd" -> SiteworxEnums.HighestCertificate.HND
                        "diploma" -> SiteworxEnums.HighestCertificate.DIPLOMA
                        "nce" -> SiteworxEnums.HighestCertificate.NCE
                        "proficiency" -> SiteworxEnums.HighestCertificate.PROFICIENCY
                        "o'level" -> SiteworxEnums.HighestCertificate.O_LEVEL
                        else -> SiteworxEnums.HighestCertificate.OTHERS
                }
        }
        fun createProfile(jwt: String, payload: ProfileDTO): ResponseEntity<Message> {
                var artisan = artisanService.extractArtisanByJWT(jwt)
                if(artisan != null) {
                        try{
                                val profile =  Profile()
                                profile.bio = payload.bio
                                profile.tradeCategory = mappingOfPayloadStrToTradeCategoryEnum(payload.tradeCategory.lowercase())
                                profile.dateOfBirth = LocalDate.parse(payload.dateOfBirth, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                profile.gender = if(payload.gender.lowercase() == "male")  SiteworxEnums.Gender.MALE else  SiteworxEnums.Gender.FEMALE
                                profile.yearsOfExperience = payload.yearsOfExperience
                                profile.highestCertificatesObtained = mappingOfPayloadStrToHighestCertificatesObtained(payload.highestCertificatesObtained.lowercase())

                                profile.artisan = artisan
                                artisanRepo.save(artisan)


                        } catch (e: Exception) {
                                return ResponseEntity.badRequest().body(Message(false, "Error occured while updating artisan profile", null))
                        }
                        return ResponseEntity.ok().body(Message(true, "Artisan Found", returnedProfile))
                }
                return ResponseEntity.ok().body(Message(true, "Artisan Found", returnedProfile))
        }

        fun updateProfile(jwt: String, payload: ProfileDTO): ResponseEntity<Message>{
                val artisan = artisanService.extractArtisanByJWT(jwt)
                val profile = artisan?.let { profileRepo.findByArtisan(it) }

                if(profile != null) {
                        when {
                                payload.bio.isNotBlank() -> profile.bio = payload.bio
                                payload.yearsOfExperience > 0 -> profile.yearsOfExperience = payload.yearsOfExperience
                                payload.tradeCategory.isNotBlank() -> profile.tradeCategory = mappingOfPayloadStrToTradeCategoryEnum(payload.tradeCategory.lowercase())
                                payload.gender.isNotBlank() -> profile.gender = if(payload.gender.lowercase() == "male")  SiteworxEnums.Gender.MALE else  SiteworxEnums.Gender.FEMALE
                                payload.dateOfBirth.isNotBlank() -> profile.dateOfBirth =  LocalDate.parse(payload.dateOfBirth, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                payload.highestCertificatesObtained.isNotBlank() -> profile.highestCertificatesObtained = mappingOfPayloadStrToHighestCertificatesObtained(payload.highestCertificatesObtained.lowercase())
                        }
                        try{
                                profile.artisan = artisan
                                artisanRepo.save(artisan)

                        } catch(e: Exception){
                                return ResponseEntity.badRequest().body(Message(false, "Error occur while updating user profile", null))
                        }
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(false, "Artisan not found", null))
                }
                return ResponseEntity.ok().body(Message(true, "Artisan Found", profile))
        }

        fun deleteArtisanProfile(jwt: String, payload: ProfileDTO): ResponseEntity<Message>{
                val artisan = artisanService.extractArtisanByJWT(jwt)
                val profile = artisan?.let { profileRepo.findByArtisan(it) }
                if(profile != null) {
                        try {
                                profileRepo.delete(profile)
                        } catch(e: Exception){
                                return ResponseEntity.badRequest().body(Message(false, "Error occur while deleting artisan's profile", null))
                        }
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(true, "Profile not found", null))
                    }
                return ResponseEntity.ok().body(Message(true, "Artisan's profile deleted successfully", artisan))
                }

//        @PostMapping("/upload")
//        fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
                // Implementation logic for file upload service function
//                return ResponseEntity.ok("File uploaded successfully")
//        }

        fun saveFile(file: MultipartFile, jwt: String): ResponseEntity<Message>{
                val artisan = artisanService.extractArtisanByJWT(jwt)
                if(artisan!= null) {
                        return try{
                                val fileName = "${artisan.id}_${file.originalFilename}"
                                val filePath = Paths.get("./uploads/$fileName")
                                Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
                                val profile = artisan.let { profileRepo.findByArtisan(it) }
                                profile.let {
                                        if (it != null) {
                                                it.photoUrl = filePath.toUri().toString()
                                        }
                                }
                                ResponseEntity.ok().body(Message(true, "Artisan's profile passport uploaded successfully", artisan))
                        } catch(e: Exception){
                                ResponseEntity.badRequest().body(Message(false, "Error occur while uploading artisan's profile picture", null))
                        }

                } else
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message(true, "Profile not found", null))

        }
}

