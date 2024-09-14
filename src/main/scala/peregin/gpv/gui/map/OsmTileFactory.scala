package peregin.gpv.gui.map

import org.jxmapviewer.viewer.{DefaultTileFactory, TileFactoryInfo}

// open street map
class OsmTileFactory extends DefaultTileFactory(new MapQuestTileInfo)

class OsmTileInfo extends TileFactoryInfo(
  0, // Minimum zoom level
  17, // Maximum zoom level
  2, // Total zoom level count
  256, // Tile size in pixels
  true, // X axis is tiled
  true, // Y axis is tiled
  "https://tile.openstreetmap.org/", // Base tile URL
  "x", "y", "z" // Tile URL parameters
);
