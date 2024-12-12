import tensorflow as tf
from tensorflow.keras.models import load_model
from tensorflow.keras.utils import img_to_array, load_img
from flask import Flask, request, jsonify
import numpy as np

model = load_model("myagara_final_model.h5")

class_names = [
    "Apple___Apple_scab",
    "Blueberry___healthy",
    "Cherry___healthy",
    "Corn___Cercospora_leaf_spot Gray_leaf_spot",
    "Grape___Black_rot",
]

def preprocess_image(image_path):
    img = load_img(image_path, target_size=(128, 128))  
    img_array = img_to_array(img) / 255.0
    img_array = np.expand_dims(img_array, axis=0)
    return img_array

def classify_image(image_path):
    image = preprocess_image(image_path)
    predictions = model.predict(image)
    predicted_class = np.argmax(predictions[0])
    confidence = np.max(predictions[0]) * 100
    return {"label": class_names[predicted_class], "confidence": round(confidence, 2)}

app = Flask(__name__)

@app.route('/classify', methods=['POST'])
def classify():
    try:

        print(request.files)


        file = request.files.get('image')
        if not file:
            return jsonify({"error": "No image file provided"}), 400

        print(f"Processing file: {file.filename}")
        file.save("temp.jpg")


        result = classify_image("temp.jpg")
        return jsonify(result)
    except Exception as e:
        print(f"Error: {str(e)}")  
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    app.run(port=5000)
