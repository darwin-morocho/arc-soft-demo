package fit.codergym.arc_soft_demo.data

class FacesDatabase {
    companion object {
        val faces = mutableMapOf<String, ByteArray>()

        /**
         * Clears the face database by removing all registered face features.
         */
        fun clearDatabase() {
            faces.clear()
        }

        /**
         * Adds a face feature to the database with an associated tag.
         *
         * @param userId The unique identifier for the user.
         * @param template The byte array containing the face feature data.
         */
        fun addFace(userId: String, template: ByteArray) {
            faces[userId] = template
        }

        /**
         * Adds multiple face features to the database.
         *
         * @param faces A map where the key is the user ID and the value is the byte array containing the face feature data.
         */
        fun addFaces(faces: Map<String, ByteArray>) {
            this.faces.putAll(faces)
        }

        /**
         * Removes a face feature from the database using the provided user ID.
         *
         * @param userId The unique identifier for the user whose face feature is to be removed.
         */
        fun removeFace(userId: String) {
            faces.remove(userId)
        }
    }
}