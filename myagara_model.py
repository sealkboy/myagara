import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten, Conv2D, MaxPooling2D, Dropout
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint

# Load the dataset
dataset_dir = "C:/tf_datasets"  # Update this path to your dataset location
ds_train = tf.keras.preprocessing.image_dataset_from_directory(
    dataset_dir,
    labels='inferred',
    label_mode='int',
    image_size=(128, 128),  # Updated image size
    batch_size=32,  # Adjust batch size as per your GPU capacity
    subset="training",
    validation_split=0.2,
    seed=123
)
ds_val = tf.keras.preprocessing.image_dataset_from_directory(
    dataset_dir,
    labels='inferred',
    label_mode='int',
    image_size=(128, 128),  # Updated image size
    batch_size=32,
    subset="validation",
    validation_split=0.2,
    seed=123
)

# Class names based on your dataset structure
class_names = ds_train.class_names
print(f"Class Names: {class_names}")

# Data augmentation
data_augmentation = tf.keras.Sequential([
    tf.keras.layers.RandomFlip("horizontal_and_vertical"),
    tf.keras.layers.RandomRotation(0.2),
    tf.keras.layers.RandomZoom(0.2)
])

# Apply data augmentation to the training dataset
ds_train = ds_train.map(lambda x, y: (data_augmentation(x), y))
ds_train = ds_train.prefetch(tf.data.AUTOTUNE)
ds_val = ds_val.prefetch(tf.data.AUTOTUNE)

# Define the model
model = Sequential([
    Conv2D(32, (3, 3), activation='relu', input_shape=(128, 128, 3)),  # Updated input shape
    MaxPooling2D((2, 2)),
    Dropout(0.2),
    Conv2D(64, (3, 3), activation='relu'),
    MaxPooling2D((2, 2)),
    Dropout(0.2),
    Conv2D(128, (3, 3), activation='relu'),
    MaxPooling2D((2, 2)),
    Dropout(0.2),
    Flatten(),
    Dense(128, activation='relu'),
    Dropout(0.5),
    Dense(len(class_names), activation='softmax')  # Output layer matches number of classes
])

# Compile the model
model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

# Define callbacks
callbacks = [
    EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True),
    ModelCheckpoint("best_plant_disease_model.keras", save_best_only=True, monitor='val_loss')
]

# Train the model
model.fit(ds_train, validation_data=ds_val, epochs=20, callbacks=callbacks)

# Save the final model
model.save("myagara_final_model.h5")
print("Model saved successfully.")
