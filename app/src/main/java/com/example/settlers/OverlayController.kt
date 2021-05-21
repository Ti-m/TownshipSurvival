package com.example.settlers

class OverlayController {

    //Add the coordinates which should be highlighted on the map. This can be Worker assignments or other stuff
    private val overlayList = mutableListOf<Coordinates>()

    fun updateOverlay(content: List<Coordinates>) {
        clearOverlay()
        overlayList.addAll(content)
    }

    fun updateOverlay(content: Coordinates) {
        clearOverlay()
        overlayList.add(content)
    }

    fun shallDrawOverlayForCoordinates(coordinates: Coordinates): Boolean {
        return overlayList.contains(coordinates)
    }

    fun clearOverlay() {
        overlayList.clear()
    }
}
