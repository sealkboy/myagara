import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.utils import img_to_array, load_img
from flask import Flask, request, jsonify
import numpy as np

# Load the trained model
model = load_model("my_trained_model.h5")

# Class names (update based on your dataset)
class_names = ["Corn__Cercospora_leaf_spot", "Corn__Common_rust", "Corn__Northern_Leaf_Blight", "Corn__healthy"]

# Preprocess image for the model
def preprocess_image(image_path):
    img = load_img(image_path, target_size=(32, 32))
    img_array = img_to_array(img) / 255.0
    img_array = np.expand_dims(img_array, axis=0)  # Add batch dimension
    return img_array

# Classify the image
def classify_image(image_path):
    image = preprocess_image(image_path)
    predictions = model.predict(image)
    predicted_class = np.argmax(predictions[0])
    confidence = np.max(predictions[0]) * 100
    return {"label": class_names[predicted_class], "confidence": round(confidence, 2)}

# Flask server
app = Flask(__name__)

@app.route('/classify', methods=['POST'])
def classify():
    try:
        # Save the uploaded image
        file = request.files['image']
        file.save("temp.jpg")
        # Process the image and return the result
        result = classify_image("temp.jpg")
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
