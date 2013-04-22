RobotVision
===========

This project describes the participation at the ImageCLEF 2012 (http://www.imageclef.org/) competition, on the Robot Vision track. The system developed is capable of making automated visual place classification of an indoor environment, based on Machine Learning and Image Processing techniques.

Method Description: 
An SVM classifier trained with CRFH (Composed Receptive Field Histograms) image features extracted from the training sets was used for the first raw classification of the test images. If the confidence score of the classifier was lower than a certain threshold, then the images were matched using ASIFT (Affine SIFT) matching algorithm. The training images were preprocessed in order extract the most representative images for each room (this was done using also the ASIFT matching algorithm) in order to lower the matching complexity.

Retrieval type: Visual

Run Type: Automatic

Other information (time for computation etc.):
The SVM training took approx. 4 hours on a Intel Core 2 Solo CPU(1.4 GHz). 
The representative images obtained from the 3 training sets using ASIFT matching algorithm took approx. 12 hours on the same configuration.
The test images were classified in approx. 22 hours on the same configuration.

Additional resources used: The training sets available on the Robot Vision 2012 official page were used for SVM classification and matching.
