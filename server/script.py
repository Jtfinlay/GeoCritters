import arcpy
import os.path
from arcpy import env

# values
env.workspace = "C:/_LOCALdata/OSM/OSM_Alberta/"
in_features = "waterways.shp"
buffer_distance = "30 Meters"
spatial_type = "water"

temp_features = "temporary.shp"
out_features = "output.shp"
template = "C:/_LOCALdata/OSM/template.shp"
projection = "C:/_LOCALdata/OSM/template.prj"


#delete temp file if exists
if arcpy.Exists(temp_features):
    arcpy.Delete_management(temp_features)
    
# create output if DNE
if not arcpy.Exists(out_features):
    arcpy.CreateFeatureclass_management(env.workspace, out_features, "POLYGON", template, "DISABLED", "DISABLED",  projection);
    

# Buffer
arcpy.Buffer_analysis(in_features, temp_features, buffer_distance)

# Cursors
bufferedFeatures = arcpy.SearchCursor(temp_features)
targetFeatures = arcpy.SearchCursor(out_features);
insertCursor = arcpy.InsertCursor(out_features);

for feature in bufferedFeatures:
    # format the output geometry
    theShape = feature.getValue("Shape")

    for ofeature in targetFeatures:
        oShape = ofeature.getValue("Shape")
        if theShape.intersect(oShape, 4):
            theShape = theShape.difference(oShape);

    # add as new feature
    newFeature = insertCursor.newRow()
    newFeature.type = spatial_type
    newFeature.source = env.workspace + in_features
    insertCursor.insertRow(newFeature)
        
# clean up
del newFeature
del insertCursor

 
